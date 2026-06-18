namespace Dashboard.Models;

public class StoredPurchaseConfirmationDetails : NotificationEventDetailsBase
{
    public required string PurchaseId { get; set; }
    public required string SessionId { get; set; }
    public int Amount { get; set; }
    public required string Status { get; set; }

    public QuizAttemptPurchaseConfirmationDto ToDto()
    {
        return new QuizAttemptPurchaseConfirmationDto
        {
            PurchaseId = PurchaseId,
            SessionId = SessionId,
            Amount = Amount,
            Status = Status
        };
    }

    public static StoredPurchaseConfirmationDetails FromDto(QuizAttemptPurchaseConfirmationDto dto)
    {
        return new StoredPurchaseConfirmationDetails
        {
            PurchaseId = dto.PurchaseId,
            SessionId = dto.SessionId,
            Amount = dto.Amount,
            Status = dto.Status
        };
    }
}
