using Dashboard.Models;

namespace Dashboard.Services;

public interface INotificationEventHandlerService
{
    Task HandleNotificationEventAsync(NotificationEvent notificationEvent);
}
