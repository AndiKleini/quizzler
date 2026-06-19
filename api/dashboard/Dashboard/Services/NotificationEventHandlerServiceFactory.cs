using Dashboard.Repositories;

namespace Dashboard.Services;

public class NotificationEventHandlerServiceFactory : INotificationEventHandlerServiceFactory
{
    private readonly ISessionDashboardRepository _sessionDashboardRepository;
    private readonly IServiceProvider _serviceProvider;

    public NotificationEventHandlerServiceFactory(
        ISessionDashboardRepository sessionDashboardRepository,
        IServiceProvider serviceProvider)
    {
        _sessionDashboardRepository = sessionDashboardRepository;
        _serviceProvider = serviceProvider;
    }

    public async Task<INotificationEventHandlerService> GetHandlerAsync(string sessionId)
    {
        var existingSession = await _sessionDashboardRepository.GetDashboardDataByDashboardIdAsync(sessionId);

        if (existingSession != null)
        {
            return _serviceProvider.GetRequiredService<NotificationEventHandlerService>();
        }
        else
        {
            return _serviceProvider.GetRequiredService<StreamNotificationEventHandlerService>();
        }
    }
}
