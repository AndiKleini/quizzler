namespace Dashboard.Services;

public interface INotificationEventHandlerServiceFactory
{
    Task<INotificationEventHandlerService> CreateHandlerAsync(string sessionId);
}
