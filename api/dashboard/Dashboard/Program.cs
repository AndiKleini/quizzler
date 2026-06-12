using Dashboard.Data;
using Dashboard.Messaging;
using Dashboard.Repositories;
using Dashboard.Services;
using Microsoft.EntityFrameworkCore;

var builder = WebApplication.CreateBuilder(args);

// Add services to the container.
builder.Services.AddDbContext<DashboardDbContext>(options =>
    options.UseSqlServer(builder.Configuration.GetConnectionString("DefaultConnection")));

builder.Services.AddScoped<ISessionDashboardRepository, SessionDashboardRepository>();
builder.Services.AddScoped<INotificationEventHandlerService, NotificationEventHandlerService>();

// Register RabbitMQ listener as hosted service
builder.Services.AddHostedService<NotificationEventListener>();

builder.Services.AddControllers();
// Learn more about configuring OpenAPI at https://aka.ms/aspnet/openapi
builder.Services.AddOpenApi();

var app = builder.Build();

// Configure the HTTP request pipeline.
if (app.Environment.IsDevelopment())
{
    app.MapOpenApi();
}

app.UseHttpsRedirection();

app.UseAuthorization();

app.MapControllers();

app.Run();
