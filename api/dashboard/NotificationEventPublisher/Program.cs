using System.Text;
using System.Text.Json;
using RabbitMQ.Client;

Console.WriteLine("=== Notification Event Publisher ===");
Console.WriteLine("Publishing test events to RabbitMQ every 5 seconds...");
Console.WriteLine("Press Ctrl+C to stop");
Console.WriteLine();

const string hostname = "localhost";
const string exchangeName = "quizzler.exchange";
const string routingKey = "quizzler.notifications";

var factory = new ConnectionFactory { HostName = hostname };
await using var connection = await factory.CreateConnectionAsync();
await using var channel = await connection.CreateChannelAsync();

await channel.ExchangeDeclareAsync(
    exchange: exchangeName,
    type: ExchangeType.Topic,
    durable: true,
    autoDelete: false);

Console.WriteLine($"Connected to RabbitMQ at {hostname}");
Console.WriteLine($"Publishing to exchange: {exchangeName}");
Console.WriteLine($"Routing key: {routingKey}");
Console.WriteLine();

var eventTypes = new[] { 1, 2, 3, 4, 5 };
var sessionIds = new[] { "session-001", "session-002", "session-003" };
var detailsTemplates = new[]
{
    "Payment received",
    "Quiz started",
    "Answer submitted",
    "Quiz completed",
    "Session updated"
};

var random = new Random();
var messageCounter = 0;

while (true)
{
    messageCounter++;

    var notificationEvent = new
    {
        SessionId = sessionIds[random.Next(sessionIds.Length)],
        Type = eventTypes[random.Next(eventTypes.Length)],
        Details = detailsTemplates[random.Next(detailsTemplates.Length)],
        TimeStamp = DateTime.UtcNow
    };

    var json = JsonSerializer.Serialize(notificationEvent, new JsonSerializerOptions
    {
        PropertyNamingPolicy = JsonNamingPolicy.CamelCase
    });

    var body = Encoding.UTF8.GetBytes(json);

    await channel.BasicPublishAsync(
        exchange: exchangeName,
        routingKey: routingKey,
        body: body);

    Console.WriteLine($"[{messageCounter:D4}] Published: {json}");

    await Task.Delay(5000);
}
