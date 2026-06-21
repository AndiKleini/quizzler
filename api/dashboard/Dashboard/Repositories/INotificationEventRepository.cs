
using Dashboard.Models;

public interface INotificationEventRepository
{
  Task AddAsync(NotificationEvent receivedEvent);
}
