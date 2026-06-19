using System.Text;
using System.Text.Json;
using Dashboard.Models;
using Dashboard.Services;
using RabbitMQ.Client;
using RabbitMQ.Client.Events;

namespace Dashboard.Messaging;

public class NotificationEventListener : BackgroundService
{
    private readonly ILogger<NotificationEventListener> _logger;
    private readonly IServiceProvider _serviceProvider;
    private readonly string _hostname;
    private readonly string _username;
    private readonly string _password;
    private readonly string _virtualHost;
    private readonly string _queueName;
    private readonly string _exchangeName;
    private readonly string _routingKey;
    private IConnection? _connection;
    private IChannel? _channel;

    public NotificationEventListener(
        ILogger<NotificationEventListener> logger,
        IServiceProvider serviceProvider,
        IConfiguration configuration)
    {
        _logger = logger;
        _serviceProvider = serviceProvider;
        _hostname = configuration.GetValue<string>("RabbitMQ:Hostname") ?? "localhost";
        _username = configuration.GetValue<string>("RabbitMQ:Username") ?? "quizzler-mq";
        _password = configuration.GetValue<string>("RabbitMQ:Password") ?? "quizzler-mq";
        _virtualHost = configuration.GetValue<string>("RabbitMQ:VirtualHost") ?? "quizzler";
        _queueName = configuration.GetValue<string>("RabbitMQ:QueueName") ?? "quizzler.notifications";
        _exchangeName = configuration.GetValue<string>("RabbitMQ:ExchangeName") ?? "quizzler.exchange";
        _routingKey = configuration.GetValue<string>("RabbitMQ:RoutingKey") ?? "quizzler.notifications";
    }

    protected override async Task ExecuteAsync(CancellationToken stoppingToken)
    {
        try
        {
            var factory = new ConnectionFactory
            {
                HostName = _hostname,
                UserName = _username,
                Password = _password,
                VirtualHost = _virtualHost
            };
            _connection = await factory.CreateConnectionAsync(stoppingToken);
            _channel = await _connection.CreateChannelAsync(cancellationToken: stoppingToken);

            await _channel.ExchangeDeclareAsync(
                exchange: _exchangeName,
                type: ExchangeType.Topic,
                durable: true,
                autoDelete: false,
                arguments: null,
                cancellationToken: stoppingToken);

            await _channel.QueueDeclareAsync(
                queue: _queueName,
                durable: true,
                exclusive: false,
                autoDelete: false,
                arguments: null,
                cancellationToken: stoppingToken);

            await _channel.QueueBindAsync(
                queue: _queueName,
                exchange: _exchangeName,
                routingKey: _routingKey,
                arguments: null,
                cancellationToken: stoppingToken);

            var consumer = new AsyncEventingBasicConsumer(_channel);
            consumer.ReceivedAsync += async (model, ea) =>
            {
                try
                {
                    var body = ea.Body.ToArray();
                    var message = Encoding.UTF8.GetString(body);

                    _logger.LogInformation("Received message from queue: {Message}", message);

                    var options = new JsonSerializerOptions
                    {
                        PropertyNamingPolicy = JsonNamingPolicy.CamelCase
                    };
                    var notificationEvent = JsonSerializer.Deserialize<NotificationEvent>(message, options);

                    if (notificationEvent != null)
                    {
                        using var scope = _serviceProvider.CreateScope();
                        var handlerFactory = scope.ServiceProvider
                            .GetRequiredService<INotificationEventHandlerServiceFactory>();

                        var handlerService = await handlerFactory.GetHandlerAsync(notificationEvent.SessionId);

                        await handlerService.HandleNotificationEventAsync(notificationEvent);

                        await _channel.BasicAckAsync(ea.DeliveryTag, false, stoppingToken);
                    }
                    else
                    {
                        _logger.LogWarning("Failed to deserialize notification event");
                        await _channel.BasicNackAsync(ea.DeliveryTag, false, false, stoppingToken);
                    }
                }
                catch (Exception ex)
                {
                    _logger.LogError(ex, "Error processing notification event");
                    await _channel.BasicNackAsync(ea.DeliveryTag, false, false, stoppingToken);
                }
            };

            await _channel.BasicConsumeAsync(
                queue: _queueName,
                autoAck: false,
                consumer: consumer,
                cancellationToken: stoppingToken);

            _logger.LogInformation(
                "NotificationEventListener started. Listening on queue '{QueueName}' with routing key '{RoutingKey}'",
                _queueName,
                _routingKey);

            await Task.Delay(Timeout.Infinite, stoppingToken);
        }
        catch (Exception ex)
        {
            _logger.LogError(ex, "Error in NotificationEventListener");
        }
    }

    public override async Task StopAsync(CancellationToken cancellationToken)
    {
        _logger.LogInformation("NotificationEventListener stopping");

        if (_channel != null)
        {
            await _channel.CloseAsync(cancellationToken);
            await _channel.DisposeAsync();
        }

        if (_connection != null)
        {
            await _connection.CloseAsync(cancellationToken);
            await _connection.DisposeAsync();
        }

        await base.StopAsync(cancellationToken);
    }
}
