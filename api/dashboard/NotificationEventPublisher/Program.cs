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

var factory = new ConnectionFactory { HostName = hostname, UserName = "quizzler-mq", Password = "quizzler-mq" };
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

var sessionIds = new[] { "session-001", "session-002", "session-003" };
var random = new Random();
var messageCounter = 0;

while (true)
{
    messageCounter++;

    // Randomly choose between PurchaseConfirmation (Type 1) and Answer (Type 2)
    var eventType = random.Next(1, 3);

    string details;
    if (eventType == 1) // PurchaseConfirmation
    {
        var purchaseConfirmation = new
        {
            PurchaseId = $"purchase-{Guid.NewGuid():N}",
            SessionId = sessionIds[random.Next(sessionIds.Length)],
            Amount = random.Next(50, 500),
            Status = "Confirmed"
        };
        details = JsonSerializer.Serialize(purchaseConfirmation, new JsonSerializerOptions
        {
            PropertyNamingPolicy = JsonNamingPolicy.CamelCase
        });
    }
    else // Answer (Type 2)
    {
        var answer = new
        {
            QuestionId = $"question-{random.Next(1, 100):D3}",
            SelectedOptionId = $"option-{random.Next(1, 5):D3}",
            IsCorrect = random.Next(0, 2) == 1
        };
        details = JsonSerializer.Serialize(answer, new JsonSerializerOptions
        {
            PropertyNamingPolicy = JsonNamingPolicy.CamelCase
        });
    }

    var notificationEvent = new
    {
        SessionId = sessionIds[random.Next(sessionIds.Length)],
        Type = eventType,
        Details = details,
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

    var eventTypeName = eventType == 1 ? "PurchaseConfirmation" : "Answer";
    Console.WriteLine($"[{messageCounter:D4}] {eventTypeName}: {json}");

    await Task.Delay(5000);
}
