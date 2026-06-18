namespace Dashboard.Models;

public class StoredAnswerDetails : NotificationEventDetailsBase
{
    public required string QuestionId { get; set; }
    public required string SelectedOptionId { get; set; }
    public bool IsCorrect { get; set; }

    public AnswerDto ToDto()
    {
        return new AnswerDto
        {
            QuestionId = QuestionId,
            SelectedOptionId = SelectedOptionId,
            IsCorrect = IsCorrect
        };
    }

    public static StoredAnswerDetails FromDto(AnswerDto dto)
    {
        return new StoredAnswerDetails
        {
            QuestionId = dto.QuestionId,
            SelectedOptionId = dto.SelectedOptionId,
            IsCorrect = dto.IsCorrect
        };
    }
}
