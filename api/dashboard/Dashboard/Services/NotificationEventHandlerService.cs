using System.Text.Json;
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
        _logger.LogInformation(
            "Received notification event - SessionId: {SessionId}, Type: {Type}, Details: {Details}, TimeStamp: {TimeStamp}",
            notificationEvent.SessionId,
            notificationEvent.Type,
            notificationEvent.Details,
            notificationEvent.TimeStamp);

        var dashboardData = await _sessionDashboardRepository.GetDashboardDataByDashboardIdAsync(
            notificationEvent.SessionId);
        if (dashboardData == null)
        {
            _logger.LogWarning("No dashboard data found for SessionId: {SessionId}", notificationEvent.SessionId);
            return;
        }

        try
        {
            ISessionDashboardUpdate? update = DeserializeDetails(notificationEvent.Type, notificationEvent.Details);

            if (update != null)
            {
                update.ApplyTo(dashboardData);
                await _sessionDashboardRepository.UpdateDashboardDataAsync(dashboardData);
                _logger.LogInformation(
                    "Updated dashboard data for SessionId: {SessionId} with event type: {Type}",
                    notificationEvent.SessionId,
                    (NotificationEventType)notificationEvent.Type);
            }
            else
            {
                _logger.LogWarning(
                    "Failed to deserialize details for event type: {Type}",
                    notificationEvent.Type);
            }
        }
        catch (JsonException ex)
        {
            _logger.LogError(ex,
                "JSON deserialization error for event type: {Type}, Details: {Details}",
                notificationEvent.Type,
                notificationEvent.Details);
        }
        catch (Exception ex)
        {
            _logger.LogError(ex,
                "Error processing notification event for SessionId: {SessionId}",
                notificationEvent.SessionId);
        }
    }

    private ISessionDashboardUpdate? DeserializeDetails(int type, string details)
    {
        var eventType = (NotificationEventType)type;

        return eventType switch
        {
            NotificationEventType.PurchaseConfirmation =>
                JsonSerializer.Deserialize<QuizAttemptPurchaseConfirmationDto>(details, new JsonSerializerOptions
                {
                    PropertyNameCaseInsensitive = true
                }),
            NotificationEventType.Answer =>
                JsonSerializer.Deserialize<AnswerDto>(details, new JsonSerializerOptions
                {
                    PropertyNameCaseInsensitive = true
                }),
            _ => null
        };
    }
}