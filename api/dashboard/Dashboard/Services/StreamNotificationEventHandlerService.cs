using Dashboard.Models;
using Dashboard.Services;

public class StreamNotificationEventHandlerService : INotificationEventHandlerService
{
  private INotificationEventRepository repository;

  public StreamNotificationEventHandlerService(INotificationEventRepository repository)
  {
    this.repository = repository;
  }

  public async Task HandleNotificationEventAsync(NotificationEvent notificationEvent)
  {
    throw new NotImplementedException();
  }
}
