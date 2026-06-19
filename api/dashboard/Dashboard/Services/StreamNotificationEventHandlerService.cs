using Dashboard.Models;
using Dashboard.Repositories;

namespace Dashboard.Services;

public class StreamNotificationEventHandlerService : INotificationEventHandlerService
{
    private readonly ILogger<StreamNotificationEventHandlerService> _logger;
    private readonly IStoredNotificationEventRepository _storedNotificationEventRepository;

    public StreamNotificationEventHandlerService(
        ILogger<StreamNotificationEventHandlerService> logger,
        IStoredNotificationEventRepository storedNotificationEventRepository)
    {
        _logger = logger;
        _storedNotificationEventRepository = storedNotificationEventRepository;
    }

    public async Task HandleNotificationEventAsync(NotificationEvent notificationEvent)
    {
        _logger.LogInformation(
            "Storing notification event as stream - SessionId: {SessionId}, Type: {Type}, TimeStamp: {TimeStamp}",
            notificationEvent.SessionId,
            notificationEvent.Type,
            notificationEvent.TimeStamp);

        try
        {
            var storedEvent = new StoredNotificationEvent
            {
                SessionId = notificationEvent.SessionId,
                Type = notificationEvent.Type,
                Details = notificationEvent.Details,
                TimeStamp = notificationEvent.TimeStamp
            };

            await _storedNotificationEventRepository.AddEventAsync(storedEvent);

            _logger.LogInformation(
                "Successfully stored notification event for SessionId: {SessionId}",
                notificationEvent.SessionId);
        }
        catch (Exception ex)
        {
            _logger.LogError(ex,
                "Error storing notification event for SessionId: {SessionId}",
                notificationEvent.SessionId);
            throw;
        }
    }
}
