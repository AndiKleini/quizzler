# Notification Event Publisher

Test utility that publishes notification events to RabbitMQ in an endless loop.

## Purpose

This console application simulates event producers by publishing test `NotificationEvent` messages to the `quizzler.notifications` queue every 5 seconds.

## Configuration

The publisher is configured with the following defaults:

- **RabbitMQ Host**: localhost
- **Exchange**: quizzler.exchange (topic)
- **Routing Key**: quizzler.notifications
- **Interval**: 5 seconds between messages

## Event Format

Each published event contains:

```json
{
  "sessionId": "session-001",
  "type": 2,
  "details": "Quiz started",
  "timeStamp": "2026-06-12T10:30:00.123Z"
}
```

### Test Data

The publisher randomly selects from:

- **Session IDs**: session-001, session-002, session-003
- **Types**: 1, 2, 3, 4, 5
- **Details**: Payment received, Quiz started, Answer submitted, Quiz completed, Session updated

## Usage

### Prerequisites

- RabbitMQ running on localhost (default port 5672)
- .NET 10 SDK

### Run

```bash
cd NotificationEventPublisher
dotnet run
```

### Output

```
=== Notification Event Publisher ===
Publishing test events to RabbitMQ every 5 seconds...
Press Ctrl+C to stop

Connected to RabbitMQ at localhost
Publishing to exchange: quizzler.exchange
Routing key: quizzler.notifications

[0001] Published: {"sessionId":"session-002","type":3,"details":"Answer submitted","timeStamp":"2026-06-12T10:30:00.123Z"}
[0002] Published: {"sessionId":"session-001","type":1,"details":"Payment received","timeStamp":"2026-06-12T10:30:05.456Z"}
...
```

### Stop

Press `Ctrl+C` to stop the publisher.

## Testing the Dashboard API

1. Start RabbitMQ (e.g., via Docker: `docker run -d -p 5672:5672 -p 15672:15672 rabbitmq:3-management`)
2. Start the Dashboard API: `cd ../Dashboard && dotnet run`
3. Start this publisher: `dotnet run`
4. Watch the Dashboard API logs to see events being processed

The Dashboard API's `NotificationEventListener` will receive and process each event through the `NotificationEventHandlerService`.
