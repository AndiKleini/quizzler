using Dashboard.Data;
using Dashboard.Models;

namespace Dashboard.Services;

public class StreamNotificationEventHandlerService : INotificationEventHandlerService
{
    private readonly ILogger<StreamNotificationEventHandlerService> _logger;
    private readonly DashboardDbContext _dbContext;

    public StreamNotificationEventHandlerService(
        ILogger<StreamNotificationEventHandlerService> logger,
        DashboardDbContext dbContext)
    {
        _logger = logger;
        _dbContext = dbContext;
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
            var storedEvent = StoredNotificationEvent.FromNotificationEvent(notificationEvent);

            _dbContext.StoredNotificationEvents.Add(storedEvent);
            await _dbContext.SaveChangesAsync();

            _logger.LogInformation(
                "Successfully stored notification event with Id: {Id} for SessionId: {SessionId}",
                storedEvent.Id,
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
