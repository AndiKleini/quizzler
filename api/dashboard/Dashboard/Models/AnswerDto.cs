namespace Dashboard.Models;

public class AnswerDto : ISessionDashboardUpdate
{
    public string QuestionId { get; set; } = string.Empty;
    public string SelectedOptionId { get; set; } = string.Empty;
    public bool IsCorrect { get; set; }

    public void ApplyTo(SessionDashboardData dashboard, DateTime timeStamp)
    {
        // TODO: Implement logic to update dashboard based on answer
        // Placeholder implementation
        if (IsCorrect)
        {
            dashboard.CorrectAnswers += 1;
        }
        else
        {
            dashboard.WrongAnswers += 1;
        }
        dashboard.Questions += 1;

        dashboard.Answers.Add(new Tuple<DateTime, AnswerDto>(timeStamp, this));
    }
}
