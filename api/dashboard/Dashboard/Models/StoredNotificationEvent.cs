using System.Text.Json;

namespace Dashboard.Models;

public class StoredNotificationEvent
{
    public int Id { get; set; }
    public required string SessionId { get; set; }
    public int Type { get; set; }
    public required string Details { get; set; }
    public DateTime TimeStamp { get; set; }

    public T? GetDetailsAs<T>() where T : class
    {
        if (string.IsNullOrWhiteSpace(Details))
        {
            return null;
        }

        return JsonSerializer.Deserialize<T>(Details, new JsonSerializerOptions
        {
            PropertyNameCaseInsensitive = true
        });
    }

    public ISessionDashboardUpdate? GetTypedDetails()
    {
        var eventType = (NotificationEventType)Type;

        return eventType switch
        {
            NotificationEventType.PurchaseConfirmation => GetDetailsAs<QuizAttemptPurchaseConfirmationDto>(),
            NotificationEventType.Answer => GetDetailsAs<AnswerDto>(),
            _ => null
        };
    }

    public static StoredNotificationEvent FromNotificationEvent(NotificationEvent notificationEvent)
    {
        return new StoredNotificationEvent
        {
            SessionId = notificationEvent.SessionId,
            Type = notificationEvent.Type,
            Details = notificationEvent.Details,
            TimeStamp = notificationEvent.TimeStamp
        };
    }
}
