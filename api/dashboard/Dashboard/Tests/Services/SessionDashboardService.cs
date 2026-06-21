using System.ComponentModel;
using System.Text.Json;
using Dashboard.Models;

internal class SessionDashboardService
{
  private static readonly JsonSerializerOptions JsonOptions = new()
  {
      PropertyNameCaseInsensitive = true  // case SENSITIVE (this is actually the default)
  };

  private INotificationEventRepository repository;

  public SessionDashboardService(INotificationEventRepository repository)
  {
    this.repository = repository;
  }

  public async Task<SessionDashboardData> GetDashboardFromNotificationEvents(string dashboardId)
  {
     SessionDashboardData dashboardFromStream = new SessionDashboardData() { DashboardId = dashboardId};

     List<NotificationEvent> events = 
      await this.repository.GetNotificationEventsForDashboardId(dashboardId);

    foreach(var currEvent in events)
    {
      ISessionDashboardUpdate? update = (NotificationEventType)currEvent.Type switch
      {
        NotificationEventType.Answer => 
          JsonSerializer.Deserialize<AnswerDto>(currEvent.Details, JsonOptions),
        NotificationEventType.PurchaseConfirmation => 
          JsonSerializer.Deserialize<QuizAttemptPurchaseConfirmationDto>(currEvent.Details, JsonOptions),
        var unknown => throw new InvalidEnumArgumentException()
      };
      update?.ApplyTo(dashboardFromStream);
    }
    return dashboardFromStream;
  }
}
