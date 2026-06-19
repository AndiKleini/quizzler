using Dashboard.Models;

namespace Dashboard.Repositories;

public interface IStoredNotificationEventRepository
{
    Task AddEventAsync(StoredNotificationEvent notificationEvent);
    Task<List<StoredNotificationEvent>> GetEventsBySessionIdAsync(string sessionId);
}
