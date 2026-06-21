
using Dashboard.Models;

public interface INotificationEventRepository
{
  Task AddAsync(NotificationEvent receivedEvent);

  Task<List<NotificationEvent>> GetNotificationEventsForDashboardId(string dASHBOARD_ID);
}
