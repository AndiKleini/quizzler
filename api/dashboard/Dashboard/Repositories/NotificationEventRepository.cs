using Dashboard.Data;
using Dashboard.Models;

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
}