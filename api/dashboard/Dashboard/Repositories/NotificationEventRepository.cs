using Dashboard.Data;
using Dashboard.Models;
using Microsoft.EntityFrameworkCore;

public class NotificationEventRepository : INotificationEventRepository
{
  private DashboardDbContext context;

  public NotificationEventRepository(DashboardDbContext context)
    {
        this.context = context;   
    }

  public async Task AddAsync(NotificationEvent receivedEvent)
  {
    await this.context.AddAsync(receivedEvent);
  }

  public async Task<List<NotificationEvent>> GetNotificationEventsForDashboardId(string dashboardId)
  {
    return await this.context.NotificationEvents.Where(e => e.SessionId == dashboardId).ToListAsync();
  }
}