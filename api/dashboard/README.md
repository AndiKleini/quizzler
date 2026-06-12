# Dashboard API

.NET 10 controller-based REST API for the Quizzler dashboard.

## Architecture

- **Framework**: ASP.NET Core Web API (.NET 10)
- **Database**: SQL Server with Entity Framework Core
- **Pattern**: Repository pattern for data access
- **Messaging**: RabbitMQ for event-driven updates

## Project Structure

```
Dashboard/
├── Controllers/
│   └── SessionDashboardController.cs  # REST endpoint
├── Controllers/
│   └── SessionDashboardController.cs  # REST endpoint
├── Data/
│   └── DashboardDbContext.cs          # EF DbContext
├── Models/
│   ├── NotificationEvent.cs           # Event model
│   └── SessionDashboardData.cs        # Entity model
├── Repositories/
│   ├── ISessionDashboardRepository.cs
│   └── SessionDashboardRepository.cs  # Repository implementation
├── Services/
│   ├── INotificationEventHandlerService.cs
│   └── NotificationEventHandlerService.cs  # Event handler
├── Messaging/
│   └── NotificationEventListener.cs   # RabbitMQ listener
└── Program.cs                         # DI and app configuration
```

## API Endpoints

### GET /api/SessionDashboard

Returns dashboard data for a session.

**Response**: 200 OK
```json
{
  "id": 1,
  "paymentAmount": 500,
  "numberOfPayments": 5,
  "wrongAnswers": 3,
  "correctAnswers": 7,
  "questions": 10
}
```

**Response**: 404 Not Found (if no data exists)

## Data Model

```csharp
public class SessionDashboardData
{
    public int Id { get; set; }
    public int PaymentAmount { get; set; }        // in cents
    public int NumberOfPayments { get; set; }
    public int WrongAnswers { get; set; }
    public int CorrectAnswers { get; set; }
    public int Questions { get; set; }
}
```

## RabbitMQ Integration

The dashboard listens to notification events from RabbitMQ:

- **Queue**: `quizzler.notifications`
- **Exchange**: `quizzler.exchange` (topic)
- **Routing Key**: `quizzler.notifications`

### Event Format

```json
{
  "sessionId": "session-123",
  "type": 1,
  "details": "Event details",
  "timeStamp": "2026-06-12T10:30:00Z"
}
```

The `NotificationEventListener` runs as a background service and automatically processes events from the queue, delegating to `NotificationEventHandlerService` for business logic.

### Testing Event Processing

A test publisher is available in `NotificationEventPublisher/` that sends test events every 5 seconds:

```bash
cd NotificationEventPublisher
dotnet run
```

See `NotificationEventPublisher/README.md` for details.

## Setup

### Prerequisites

- .NET 10 SDK
- SQL Server (local or remote)
- RabbitMQ (local or remote)

### Configuration

Update the connection string and RabbitMQ settings in `appsettings.json`:

```json
{
  "ConnectionStrings": {
    "DefaultConnection": "Server=localhost,1433;Database=dashboardDb;User Id=sa;Password=Dboard123!;TrustServerCertificate=True;"
  },
  "RabbitMQ": {
    "Hostname": "localhost",
    "QueueName": "quizzler.notifications",
    "ExchangeName": "quizzler.exchange",
    "RoutingKey": "quizzler.notifications"
  }
}
```

### Database Setup

```bash
cd Dashboard
dotnet ef database update
```

### Run

```bash
cd Dashboard
dotnet run
```

API will be available at `https://localhost:5001` (or check console output for actual port).

## Development

### Build

```bash
dotnet build
```

### Run Tests

```bash
dotnet test
```

Unit tests use:
- **NUnit 4.6.1** for test framework
- **Moq 4.20.72** for mocking
- **Shouldly 4.3.0** for fluent assertions
- **EF Core In-Memory** for repository tests

Test coverage:
- **Controllers**: `SessionDashboardControllerTests` (4 tests)
- **Repositories**: `SessionDashboardRepositoryTests` (3 tests)
- **Services**: `NotificationEventHandlerServiceTests` (7 tests)

Test naming convention: `ClassNameTests` for test class, `MethodName_Scenario_ExpectedBehavior` for test methods.

See `product/architecture/Arc42 Template in Markdown.md` → Cross-cutting Concepts → Unit testing (C#) for detailed testing guidelines.

### Create Migration

```bash
dotnet ef migrations add <MigrationName>
```

### Update Database

```bash
dotnet ef database update
```

## Dependencies

### Production
- Microsoft.EntityFrameworkCore (10.0.9)
- Microsoft.EntityFrameworkCore.Design (10.0.9)
- Microsoft.EntityFrameworkCore.SqlServer (10.0.9)
- RabbitMQ.Client (7.2.1)

### Testing
- NUnit (4.6.1)
- NUnit3TestAdapter (6.2.0)
- Microsoft.NET.Test.Sdk (18.6.0)
- Moq (4.20.72)
- Shouldly (4.3.0)
- Microsoft.EntityFrameworkCore.InMemory (10.0.9)
