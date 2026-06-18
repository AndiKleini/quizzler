using Dashboard.Models;

namespace Dashboard.Repositories;

public interface IStoredNotificationEventRepository
{
    Task<List<StoredNotificationEvent>> GetEventsBySessionIdAsync(string sessionId);
    Task<List<StoredNotificationEvent>> GetAnswerEventsBySessionIdAsync(string sessionId);
}
