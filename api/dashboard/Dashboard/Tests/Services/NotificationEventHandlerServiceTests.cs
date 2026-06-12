using Dashboard.Models;
using Dashboard.Repositories;
using Dashboard.Services;
using Microsoft.Extensions.Logging;
using Moq;
using NUnit.Framework;
using Shouldly;

namespace Dashboard.Tests.Services;

[TestFixture]
public class NotificationEventHandlerServiceTests
{
    private Mock<ILogger<NotificationEventHandlerService>> _mockLogger = null!;
    private Mock<ISessionDashboardRepository> _mockRepository = null!;
    private NotificationEventHandlerService _service = null!;

    [SetUp]
    public void SetUp()
    {
        _mockLogger = new Mock<ILogger<NotificationEventHandlerService>>();
        _mockRepository = new Mock<ISessionDashboardRepository>();
        _service = new NotificationEventHandlerService(_mockLogger.Object, _mockRepository.Object);
    }

    [Test]
    public async Task HandleNotificationEventAsync_WhenDashboardDataExists_UpdatesData()
    {
        // Arrange
        var existingData = new SessionDashboardData
        {
            Id = 1,
            PaymentAmount = 100,
            NumberOfPayments = 5,
            WrongAnswers = 2,
            CorrectAnswers = 8,
            Questions = 10
        };

        var notificationEvent = new NotificationEvent
        {
            SessionId = "session-001",
            Type = 1,
            Details = "Payment received",
            TimeStamp = DateTime.UtcNow
        };

        _mockRepository
            .Setup(repo => repo.GetDashboardDataAsync())
            .ReturnsAsync(existingData);

        // Act
        await _service.HandleNotificationEventAsync(notificationEvent);

        // Assert
        existingData.PaymentAmount.ShouldBe(110);
        existingData.NumberOfPayments.ShouldBe(6);
        existingData.WrongAnswers.ShouldBe(3);
        existingData.CorrectAnswers.ShouldBe(9);
        existingData.Questions.ShouldBe(11);
    }

    [Test]
    public async Task HandleNotificationEventAsync_WhenNoDashboardDataExists_DoesNotCallUpdate()
    {
        // Arrange
        var notificationEvent = new NotificationEvent
        {
            SessionId = "session-001",
            Type = 1,
            Details = "Payment received",
            TimeStamp = DateTime.UtcNow
        };

        _mockRepository
            .Setup(repo => repo.GetDashboardDataAsync())
            .ReturnsAsync((SessionDashboardData?)null);

        // Act
        await _service.HandleNotificationEventAsync(notificationEvent);

        // Assert
        _mockRepository.Verify(
            repo => repo.UpdateDashboardDataAsync(It.IsAny<SessionDashboardData>()),
            Times.Never);
    }
}
