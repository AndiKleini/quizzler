namespace Dashboard.Models;

public class StoredNotificationEvent
{
    public int Id { get; set; }
    public required string SessionId { get; set; }
    public int Type { get; set; }
    public DateTime TimeStamp { get; set; }

    // Navigation properties
    public StoredAnswerDetails? AnswerDetails { get; set; }
    public StoredPurchaseConfirmationDetails? PurchaseConfirmationDetails { get; set; }

    public ISessionDashboardUpdate? GetTypedDetails()
    {
        var eventType = (NotificationEventType)Type;

        return eventType switch
        {
            NotificationEventType.Answer => AnswerDetails?.ToDto(),
            NotificationEventType.PurchaseConfirmation => PurchaseConfirmationDetails?.ToDto(),
            _ => null
        };
    }

    public AnswerDto? GetAnswerDetails()
    {
        return AnswerDetails?.ToDto();
    }

    public QuizAttemptPurchaseConfirmationDto? GetPurchaseConfirmationDetails()
    {
        return PurchaseConfirmationDetails?.ToDto();
    }
}
