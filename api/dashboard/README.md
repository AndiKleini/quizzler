# Dashboard API

.NET 10 controller-based REST API for the Quizzler dashboard.

## Architecture

- **Framework**: ASP.NET Core Web API (.NET 10)
- **Database**: SQL Server with Entity Framework Core
- **Pattern**: Repository pattern for data access

## Project Structure

```
Dashboard/
├── Controllers/
│   └── SessionDashboardController.cs  # REST endpoint
├── Data/
│   └── DashboardDbContext.cs          # EF DbContext
├── Models/
│   └── SessionDashboardData.cs        # Entity model
├── Repositories/
│   ├── ISessionDashboardRepository.cs
│   └── SessionDashboardRepository.cs  # Repository implementation
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

## Setup

### Prerequisites

- .NET 10 SDK
- SQL Server (local or remote)

### Configuration

Update the connection string in `appsettings.json`:

```json
{
  "ConnectionStrings": {
    "DefaultConnection": "Server=localhost;Database=QuizzlerDashboard;Trusted_Connection=True;TrustServerCertificate=True;"
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

### Create Migration

```bash
dotnet ef migrations add <MigrationName>
```

### Update Database

```bash
dotnet ef database update
```

## Dependencies

- Microsoft.EntityFrameworkCore (10.0.9)
- Microsoft.EntityFrameworkCore.Design (10.0.9)
- Microsoft.EntityFrameworkCore.SqlServer (10.0.9)
