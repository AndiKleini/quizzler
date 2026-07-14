using Dashboard.Data;
using Dashboard.Messaging;
using Dashboard.Repositories;
using Dashboard.Services;
using Microsoft.EntityFrameworkCore;

var builder = WebApplication.CreateBuilder(args);

// Add services to the container.
var useInMemoryDatabase = builder.Configuration.GetValue<bool>("UseInMemoryDatabase");
builder.Services.AddDbContext<DashboardDbContext>(options =>
{
    if (useInMemoryDatabase)
    {
        options.UseInMemoryDatabase("DashboardInMemoryDb");
    }
    else
    {
        options.UseSqlServer(builder.Configuration.GetConnectionString("DefaultConnection"));
    }
});

builder.Services.AddScoped<ISessionDashboardRepository, SessionDashboardRepository>();
builder.Services.AddScoped<INotificationEventHandlerService, NotificationEventHandlerService>();

// Register RabbitMQ listener as hosted service
builder.Services.AddHostedService<NotificationEventListener>();

builder.Services.AddControllers();
// Learn more about configuring OpenAPI at https://aka.ms/aspnet/openapi
builder.Services.AddOpenApi();

// Add CORS policy for dashboard UI. The allowed origin(s) are environment-driven
// (comma-separated) so the same image serves docker-compose (localhost:4202) and
// KIND (the dashboard ingress host) without a rebuild. Defaults to the
// docker-compose UI origin when unset.
var corsOrigins = (builder.Configuration["Cors:AllowedOrigins"] ?? "http://localhost:4200")
    .Split(',', StringSplitOptions.RemoveEmptyEntries | StringSplitOptions.TrimEntries);

builder.Services.AddCors(options =>
{
    options.AddDefaultPolicy(policy =>
    {
        policy.WithOrigins(corsOrigins)
              .AllowAnyMethod()
              .AllowAnyHeader();
    });
});

var app = builder.Build();

// Apply pending migrations on startup (only for relational databases, not in-memory)
using (var scope = app.Services.CreateScope())
{
    var dbContext = scope.ServiceProvider.GetRequiredService<DashboardDbContext>();
    if (!useInMemoryDatabase)
    {
        dbContext.Database.Migrate();
    }
    else
    {
        // Ensure the in-memory database is created
        dbContext.Database.EnsureCreated();

        // Seed default sessions for in-memory database
        if (!dbContext.SessionDashboardData.Any())
        {
            dbContext.SessionDashboardData.AddRange(
                new Dashboard.Models.SessionDashboardData
                {
                    DashboardId = "session-demo-1",
                    PaymentAmount = 1500,
                    NumberOfPayments = 3,
                    WrongAnswers = 2,
                    CorrectAnswers = 8,
                    Questions = 10
                },
                new Dashboard.Models.SessionDashboardData
                {
                    DashboardId = "session-demo-2",
                    PaymentAmount = 2500,
                    NumberOfPayments = 5,
                    WrongAnswers = 5,
                    CorrectAnswers = 15,
                    Questions = 20
                },
                new Dashboard.Models.SessionDashboardData
                {
                    DashboardId = "session-demo-3",
                    PaymentAmount = 500,
                    NumberOfPayments = 1,
                    WrongAnswers = 3,
                    CorrectAnswers = 2,
                    Questions = 5
                }
            );
            dbContext.SaveChanges();
        }
    }
}

// Configure the HTTP request pipeline.
if (app.Environment.IsDevelopment())
{
    app.MapOpenApi();
}

app.UseHttpsRedirection();

app.UseCors();

app.UseAuthorization();

app.MapControllers();

app.Run();
