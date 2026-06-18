using Dashboard.Data;
using Dashboard.Repositories;

namespace Dashboard.Services;

public class NotificationEventHandlerServiceFactory : INotificationEventHandlerServiceFactory
{
    private readonly IServiceProvider _serviceProvider;
    private readonly ILogger<NotificationEventHandlerServiceFactory> _logger;

    public NotificationEventHandlerServiceFactory(
        IServiceProvider serviceProvider,
        ILogger<NotificationEventHandlerServiceFactory> logger)
    {
        _serviceProvider = serviceProvider;
        _logger = logger;
    }

    public async Task<INotificationEventHandlerService> CreateHandlerAsync(string sessionId)
    {
        using var scope = _serviceProvider.CreateScope();
        var repository = scope.ServiceProvider.GetRequiredService<ISessionDashboardRepository>();

        var existingSession = await repository.GetDashboardDataByDashboardIdAsync(sessionId);

        if (existingSession != null)
        {
            _logger.LogInformation(
                "Session {SessionId} exists - using aggregation handler",
                sessionId);

            return new NotificationEventHandlerService(
                _serviceProvider.GetRequiredService<ILogger<NotificationEventHandlerService>>(),
                _serviceProvider.GetRequiredService<ISessionDashboardRepository>());
        }
        else
        {
            _logger.LogInformation(
                "Session {SessionId} does not exist - using stream handler",
                sessionId);

            return new StreamNotificationEventHandlerService(
                _serviceProvider.GetRequiredService<ILogger<StreamNotificationEventHandlerService>>(),
                _serviceProvider.GetRequiredService<DashboardDbContext>());
        }
    }
}
