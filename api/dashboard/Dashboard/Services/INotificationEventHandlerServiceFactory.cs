namespace Dashboard.Services;

public interface INotificationEventHandlerServiceFactory
{
    Task<INotificationEventHandlerService> GetHandlerAsync(string sessionId);
}
