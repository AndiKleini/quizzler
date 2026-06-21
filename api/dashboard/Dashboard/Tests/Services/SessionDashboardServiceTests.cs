using Dashboard.Models;
using Moq;
using NUnit.Framework;
using Shouldly;

[TestFixture]
public class SessionDashboardServiceTests
{
    private const string DASHBOARD_ID = "SomeDashboardId";

    [Test]
    public async Task GetDashboardFromNotificationEvents_DashboardIdSupplied_ReturnsSessionDashboard()
    {
        // Capture the answer timestamps so the expected Answers list can be
        // asserted against the actual one tuple-for-tuple.
        DateTime answerTimeStamp1 = DateTime.Now;
        DateTime answerTimeStamp2 = DateTime.Now;
        DateTime answerTimeStamp3 = DateTime.Now;
        SessionDashboardData expectedDashboard =
            new SessionDashboardData()
            {
                DashboardId = DASHBOARD_ID,
                Questions = 3,
                CorrectAnswers = 2,
                WrongAnswers = 1,
                PaymentAmount = 300,
                NumberOfPayments = 2,
                Answers = new List<Tuple<DateTime, AnswerDto>>()
                {
                    new Tuple<DateTime, AnswerDto>(
                        answerTimeStamp1,
                        new AnswerDto()
                        {
                            QuestionId = "selection_1",
                            SelectedOptionId = "option_1",
                            IsCorrect = false
                        }),
                    new Tuple<DateTime, AnswerDto>(
                        answerTimeStamp2,
                        new AnswerDto()
                        {
                            QuestionId = "selection_1",
                            SelectedOptionId = "option_1",
                            IsCorrect = true
                        }),
                    new Tuple<DateTime, AnswerDto>(
                        answerTimeStamp3,
                        new AnswerDto()
                        {
                            QuestionId = "selection_1",
                            SelectedOptionId = "option_1",
                            IsCorrect = true
                        })
                }
            };
        NotificationEvent receivedEvent1 =
            new NotificationEvent()
            {
                SessionId = DASHBOARD_ID,
                TimeStamp = answerTimeStamp1,
                Details = @"{
                            ""questionId"": ""selection_1"",
                            ""isCorrect"": false,
                            ""selectedOptionId"": ""option_1""
                }",
                Type = (int)NotificationEventType.Answer
            };
        NotificationEvent receivedEvent2 =
            new NotificationEvent()
            {
                SessionId = DASHBOARD_ID,
                TimeStamp = answerTimeStamp2,
                Details = @"{
                            ""questionId"": ""selection_1"",
                            ""isCorrect"": true,
                            ""selectedOptionId"": ""option_1""
                }",
                Type = (int)NotificationEventType.Answer
            };
        NotificationEvent receivedEvent3 =
            new NotificationEvent()
            {
                SessionId = DASHBOARD_ID,
                TimeStamp = answerTimeStamp3,
                Details = @"{
                            ""questionId"": ""selection_1"",
                            ""isCorrect"": true,
                            ""selectedOptionId"": ""option_1""
                }",
                Type = (int)NotificationEventType.Answer
            };
        NotificationEvent receivedEvent4 = 
            new NotificationEvent()
            {
                SessionId = DASHBOARD_ID,
                TimeStamp = DateTime.Now, // no need to worry about flakiness here
                Details = @"{
                    ""purchaseId"": ""purchase_1"",
                    ""sessionId"": ""session_1"",
                    ""amount"": 100,
                    ""status"": ""confirmed""
                }",
                Type = (int)NotificationEventType.PurchaseConfirmation
            };
        NotificationEvent receivedEvent5 = 
            new NotificationEvent()
            {
                SessionId = DASHBOARD_ID,
                TimeStamp = DateTime.Now, // no need to worry about flakiness here
                Details = @"{
                    ""purchaseId"": ""purchase_1"",
                    ""sessionId"": ""session_1"",
                    ""amount"": 200,
                    ""status"": ""confirmed""
                }",
                Type = (int)NotificationEventType.PurchaseConfirmation
            };
        Mock<INotificationEventRepository> repository = new Mock<INotificationEventRepository>();
        repository.Setup(m => m.GetNotificationEventsForDashboardId(DASHBOARD_ID)).
            ReturnsAsync(
                new List<NotificationEvent>()
                {
                    receivedEvent1,
                    receivedEvent2,
                    receivedEvent3,
                    receivedEvent4,
                    receivedEvent5
                });
        SessionDashboardService instanceUnderTest = 
            new SessionDashboardService(repository.Object);

        var receivedSessionData = await instanceUnderTest.GetDashboardFromNotificationEvents(DASHBOARD_ID);

        receivedSessionData.ShouldBeEquivalentTo(expectedDashboard);
    }    
}