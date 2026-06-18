using Dashboard.Data;
using Dashboard.Models;
using Microsoft.EntityFrameworkCore;

namespace Dashboard.Repositories;

public class StoredNotificationEventRepository : IStoredNotificationEventRepository
{
    private readonly DashboardDbContext _context;

    public StoredNotificationEventRepository(DashboardDbContext context)
    {
        _context = context;
    }

    public async Task<List<StoredNotificationEvent>> GetEventsBySessionIdAsync(string sessionId)
    {
        return await _context.StoredNotificationEvents
            .Where(e => e.SessionId == sessionId)
            .OrderBy(e => e.TimeStamp)
            .ToListAsync();
    }

    public async Task<List<StoredNotificationEvent>> GetAnswerEventsBySessionIdAsync(string sessionId)
    {
        return await _context.StoredNotificationEvents
            .Where(e => e.SessionId == sessionId && e.Type == (int)NotificationEventType.Answer)
            .OrderBy(e => e.TimeStamp)
            .ToListAsync();
    }
}
