namespace Dashboard.Models;

public class QuizAttemptPurchaseConfirmationDto : ISessionDashboardUpdate
{
    public string PurchaseId { get; set; } = string.Empty;
    public string SessionId { get; set; } = string.Empty;
    public int Amount { get; set; }
    public string Status { get; set; } = string.Empty;

    public void ApplyTo(SessionDashboardData dashboard, DateTime timeStamp)
    {
        // TODO: Implement logic to update dashboard based on purchase confirmation
        // Placeholder implementation
        dashboard.PaymentAmount += Amount;
        dashboard.NumberOfPayments += 1;
    }
}
