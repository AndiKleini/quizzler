namespace Dashboard.Models;

public class SessionDashboardData
{
    public int Id { get; set; }
    public int PaymentAmount { get; set; }
    public int NumberOfPayments { get; set; }
    public int WrongAnswers { get; set; }
    public int CorrectAnswers { get; set; }
    public int Questions { get; set; }
}
