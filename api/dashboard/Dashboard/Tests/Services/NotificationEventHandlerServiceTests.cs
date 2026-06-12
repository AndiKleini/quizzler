using System.Text.Json;
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
    public async Task HandleNotificationEventAsync_WithPurchaseConfirmation_UpdatesPaymentData()
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

        var purchaseConfirmation = new QuizAttemptPurchaseConfirmationDto
        {
            PurchaseId = "purchase-001",
            SessionId = "session-001",
            Amount = 250,
            Status = "Confirmed"
        };

        var notificationEvent = new NotificationEvent
        {
            SessionId = "session-001",
            Type = (int)NotificationEventType.PurchaseConfirmation,
            Details = JsonSerializer.Serialize(purchaseConfirmation, new JsonSerializerOptions
            {
                PropertyNamingPolicy = JsonNamingPolicy.CamelCase
            }),
            TimeStamp = DateTime.UtcNow
        };

        _mockRepository
            .Setup(repo => repo.GetDashboardDataAsync())
            .ReturnsAsync(existingData);

        // Act
        await _service.HandleNotificationEventAsync(notificationEvent);

        // Assert
        existingData.PaymentAmount.ShouldBe(350);
        existingData.NumberOfPayments.ShouldBe(6);
    }

    [Test]
    public async Task HandleNotificationEventAsync_WithCorrectAnswer_UpdatesCorrectAnswersAndQuestions()
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

        var answer = new AnswerDto
        {
            QuestionId = "question-001",
            SelectedOptionId = "option-002",
            IsCorrect = true
        };

        var notificationEvent = new NotificationEvent
        {
            SessionId = "session-001",
            Type = (int)NotificationEventType.Answer,
            Details = JsonSerializer.Serialize(answer, new JsonSerializerOptions
            {
                PropertyNamingPolicy = JsonNamingPolicy.CamelCase
            }),
            TimeStamp = DateTime.UtcNow
        };

        _mockRepository
            .Setup(repo => repo.GetDashboardDataAsync())
            .ReturnsAsync(existingData);

        // Act
        await _service.HandleNotificationEventAsync(notificationEvent);

        // Assert
        existingData.CorrectAnswers.ShouldBe(9);
        existingData.Questions.ShouldBe(11);
        existingData.WrongAnswers.ShouldBe(2);
    }

    [Test]
    public async Task HandleNotificationEventAsync_WithWrongAnswer_UpdatesWrongAnswersAndQuestions()
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

        var answer = new AnswerDto
        {
            QuestionId = "question-001",
            SelectedOptionId = "option-003",
            IsCorrect = false
        };

        var notificationEvent = new NotificationEvent
        {
            SessionId = "session-001",
            Type = (int)NotificationEventType.Answer,
            Details = JsonSerializer.Serialize(answer, new JsonSerializerOptions
            {
                PropertyNamingPolicy = JsonNamingPolicy.CamelCase
            }),
            TimeStamp = DateTime.UtcNow
        };

        _mockRepository
            .Setup(repo => repo.GetDashboardDataAsync())
            .ReturnsAsync(existingData);

        // Act
        await _service.HandleNotificationEventAsync(notificationEvent);

        // Assert
        existingData.WrongAnswers.ShouldBe(3);
        existingData.Questions.ShouldBe(11);
        existingData.CorrectAnswers.ShouldBe(8);
    }

    [Test]
    public async Task HandleNotificationEventAsync_WithPurchaseConfirmation_CallsUpdateOnRepository()
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

        var purchaseConfirmation = new QuizAttemptPurchaseConfirmationDto
        {
            PurchaseId = "purchase-001",
            SessionId = "session-001",
            Amount = 250,
            Status = "Confirmed"
        };

        var notificationEvent = new NotificationEvent
        {
            SessionId = "session-001",
            Type = (int)NotificationEventType.PurchaseConfirmation,
            Details = JsonSerializer.Serialize(purchaseConfirmation, new JsonSerializerOptions
            {
                PropertyNamingPolicy = JsonNamingPolicy.CamelCase
            }),
            TimeStamp = DateTime.UtcNow
        };

        _mockRepository
            .Setup(repo => repo.GetDashboardDataAsync())
            .ReturnsAsync(existingData);

        // Act
        await _service.HandleNotificationEventAsync(notificationEvent);

        // Assert
        _mockRepository.Verify(
            repo => repo.UpdateDashboardDataAsync(existingData),
            Times.Once);
    }

    [Test]
    public async Task HandleNotificationEventAsync_WhenNoDashboardDataExists_DoesNotCallUpdate()
    {
        // Arrange
        var purchaseConfirmation = new QuizAttemptPurchaseConfirmationDto
        {
            PurchaseId = "purchase-001",
            SessionId = "session-001",
            Amount = 250,
            Status = "Confirmed"
        };

        var notificationEvent = new NotificationEvent
        {
            SessionId = "session-001",
            Type = (int)NotificationEventType.PurchaseConfirmation,
            Details = JsonSerializer.Serialize(purchaseConfirmation, new JsonSerializerOptions
            {
                PropertyNamingPolicy = JsonNamingPolicy.CamelCase
            }),
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

    [Test]
    public async Task HandleNotificationEventAsync_WithInvalidJson_DoesNotCallUpdate()
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
            Type = (int)NotificationEventType.PurchaseConfirmation,
            Details = "invalid json {{{",
            TimeStamp = DateTime.UtcNow
        };

        _mockRepository
            .Setup(repo => repo.GetDashboardDataAsync())
            .ReturnsAsync(existingData);

        // Act
        await _service.HandleNotificationEventAsync(notificationEvent);

        // Assert
        _mockRepository.Verify(
            repo => repo.UpdateDashboardDataAsync(It.IsAny<SessionDashboardData>()),
            Times.Never);
    }

    [Test]
    public async Task HandleNotificationEventAsync_WithUnknownEventType_DoesNotCallUpdate()
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
            Type = 999,
            Details = "{\"someData\": \"value\"}",
            TimeStamp = DateTime.UtcNow
        };

        _mockRepository
            .Setup(repo => repo.GetDashboardDataAsync())
            .ReturnsAsync(existingData);

        // Act
        await _service.HandleNotificationEventAsync(notificationEvent);

        // Assert
        _mockRepository.Verify(
            repo => repo.UpdateDashboardDataAsync(It.IsAny<SessionDashboardData>()),
            Times.Never);
    }
}
