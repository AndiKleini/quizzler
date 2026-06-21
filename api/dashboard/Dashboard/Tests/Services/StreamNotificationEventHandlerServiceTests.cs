
using Dashboard.Models;
using Dashboard.Services;
using Moq;
using NUnit.Framework;

[TestFixture]
public class StreamNotificationEventHandlerServiceTests
{
    [Test]
    public async Task HandleNotificationEventAsync_EventSupplied_ForwardedToPersist()
    {
        NotificationEvent receivedEvent = 
            new NotificationEvent()
            {
                SessionId = "SomeSessionId",
                TimeStamp = DateTime.Now, // will not cause flakyness
                Details = @"{
                            ""questionId"": ""selection_1"",
                            ""isCorrect"": false,
                            ""selectedOptionId"": ""option_1""
                }"
            };
        Mock<INotificationEventRepository> repositoryMock = 
            new Mock<INotificationEventRepository>();
        repositoryMock.Setup(m => m.AddAsync(receivedEvent)).Returns(Task.CompletedTask);
        INotificationEventHandlerService instanceUnderTest = 
            new StreamNotificationEventHandlerService(repositoryMock.Object);

        await instanceUnderTest.HandleNotificationEventAsync(receivedEvent);

        repositoryMock.Verify(m => m.AddAsync(receivedEvent), Times.Once);
    }
}
