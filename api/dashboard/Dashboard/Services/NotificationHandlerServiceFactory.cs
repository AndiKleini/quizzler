using Dashboard.Repositories;
using Dashboard.Services;

internal class NotificationHandlerServiceFactory
{
  private ISessionDashboardRepository repository;
  private readonly NotificationEventHandlerService notificationEventHandlerService;
  private readonly StreamNotificationEventHandlerService streamNotificationEventHandlerService;

  public NotificationHandlerServiceFactory(
    ISessionDashboardRepository repository,
    NotificationEventHandlerService notificationEventHandlerService,
    StreamNotificationEventHandlerService streamNotificationEventHandlerService)
  {
    this.repository = repository;
    this.notificationEventHandlerService = notificationEventHandlerService;
    this.streamNotificationEventHandlerService = streamNotificationEventHandlerService;
  }

  internal INotificationEventHandlerService GetInstance(string dashboardId)
  {
     return repository.Exists(dashboardId) ? 
        this.notificationEventHandlerService : 
        this.streamNotificationEventHandlerService;
  }
}
