using Dashboard.Models;
using Dashboard.Repositories;

namespace Dashboard.Services;

public class NotificationEventHandlerService : INotificationEventHandlerService
{
    private readonly ILogger<NotificationEventHandlerService> _logger;
    private readonly ISessionDashboardRepository _sessionDashboardRepository;

    public NotificationEventHandlerService(
        ILogger<NotificationEventHandlerService> logger, 
        ISessionDashboardRepository sessionDashboardRepository)
    {
        _sessionDashboardRepository = sessionDashboardRepository;
        _logger = logger;
    }

    public async Task HandleNotificationEventAsync(NotificationEvent notificationEvent)
    {
        // TODO: Implement logic to update DashboardSession based on NotificationEvent
        _logger.LogInformation(
            "Received notification event - SessionId: {SessionId}, Type: {Type}, Details: {Details}, TimeStamp: {TimeStamp}",
            notificationEvent.SessionId,
            notificationEvent.Type,
            notificationEvent.Details,
            notificationEvent.TimeStamp);

        var dashboardData = await _sessionDashboardRepository.GetDashboardDataAsync();
        if (dashboardData != null)
        {
                // Update the dashboard data based on the notification event
                // This is a placeholder for actual update logic
                dashboardData.PaymentAmount += 10; // Example update, replace with actual logic
                dashboardData.NumberOfPayments += 1;
                dashboardData.WrongAnswers += 1; // Example update, replace with actual logic
                dashboardData.CorrectAnswers += 1; // Example update, replace with actual logic
                dashboardData.Questions += 1; // Example update, replace with actual logic
                await _sessionDashboardRepository.UpdateDashboardDataAsync(dashboardData);
                _logger.LogInformation(
                    "Updating dashboard data for SessionId: {SessionId}", 
                    notificationEvent.SessionId);
        }
        else 
        {
            _logger.LogWarning("No dashboard data found for SessionId: {SessionId}", notificationEvent.SessionId);
        }
    }
}