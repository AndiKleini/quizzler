using System.Text.Json;
using Dashboard.Models;
using NUnit.Framework;

namespace Dashboard.Tests.Models;

/// <summary>
/// Unit tests for StoredNotificationEvent model.
///
/// CCC (Convention, Configuration, Code) Testing Principles:
/// - We do NOT test log messages to avoid red tests when only log messages change
/// - Focus on testing model behavior and data transformation
/// - Logging is considered an implementation detail
/// </summary>
[TestFixture]
public class StoredNotificationEventTests
{
    [Test]
    public void GetDetailsAs_DeserializesAnswerDto_Successfully()
    {
        // Arrange
        var answerDto = new AnswerDto
        {
            QuestionId = "question-123",
            SelectedOptionId = "option-456",
            IsCorrect = true
        };
        var details = JsonSerializer.Serialize(answerDto, new JsonSerializerOptions
        {
            PropertyNamingPolicy = JsonNamingPolicy.CamelCase
        });

        var storedEvent = new StoredNotificationEvent
        {
            SessionId = "session-001",
            Type = (int)NotificationEventType.Answer,
            Details = details,
            TimeStamp = DateTime.UtcNow
        };

        // Act
        var result = storedEvent.GetDetailsAs<AnswerDto>();

        // Assert
        Assert.That(result, Is.Not.Null);
        Assert.That(result!.QuestionId, Is.EqualTo(answerDto.QuestionId));
        Assert.That(result.SelectedOptionId, Is.EqualTo(answerDto.SelectedOptionId));
        Assert.That(result.IsCorrect, Is.EqualTo(answerDto.IsCorrect));
    }

    [Test]
    public void GetDetailsAs_DeserializesPurchaseConfirmationDto_Successfully()
    {
        // Arrange
        var purchaseDto = new QuizAttemptPurchaseConfirmationDto
        {
            PurchaseId = "purchase-789",
            SessionId = "session-001",
            Amount = 2500,
            Status = "Confirmed"
        };
        var details = JsonSerializer.Serialize(purchaseDto, new JsonSerializerOptions
        {
            PropertyNamingPolicy = JsonNamingPolicy.CamelCase
        });

        var storedEvent = new StoredNotificationEvent
        {
            SessionId = "session-001",
            Type = (int)NotificationEventType.PurchaseConfirmation,
            Details = details,
            TimeStamp = DateTime.UtcNow
        };

        // Act
        var result = storedEvent.GetDetailsAs<QuizAttemptPurchaseConfirmationDto>();

        // Assert
        Assert.That(result, Is.Not.Null);
        Assert.That(result!.PurchaseId, Is.EqualTo(purchaseDto.PurchaseId));
        Assert.That(result.SessionId, Is.EqualTo(purchaseDto.SessionId));
        Assert.That(result.Amount, Is.EqualTo(purchaseDto.Amount));
        Assert.That(result.Status, Is.EqualTo(purchaseDto.Status));
    }

    [Test]
    public void GetDetailsAs_ReturnsNull_WhenDetailsIsEmpty()
    {
        // Arrange
        var storedEvent = new StoredNotificationEvent
        {
            SessionId = "session-001",
            Type = (int)NotificationEventType.Answer,
            Details = "",
            TimeStamp = DateTime.UtcNow
        };

        // Act
        var result = storedEvent.GetDetailsAs<AnswerDto>();

        // Assert
        Assert.That(result, Is.Null);
    }

    [Test]
    public void GetDetailsAs_ReturnsNull_WhenDetailsIsWhitespace()
    {
        // Arrange
        var storedEvent = new StoredNotificationEvent
        {
            SessionId = "session-001",
            Type = (int)NotificationEventType.Answer,
            Details = "   ",
            TimeStamp = DateTime.UtcNow
        };

        // Act
        var result = storedEvent.GetDetailsAs<AnswerDto>();

        // Assert
        Assert.That(result, Is.Null);
    }

    [Test]
    public void GetTypedDetails_ReturnsAnswerDto_WhenTypeIsAnswer()
    {
        // Arrange
        var answerDto = new AnswerDto
        {
            QuestionId = "question-123",
            SelectedOptionId = "option-456",
            IsCorrect = false
        };
        var details = JsonSerializer.Serialize(answerDto, new JsonSerializerOptions
        {
            PropertyNamingPolicy = JsonNamingPolicy.CamelCase
        });

        var storedEvent = new StoredNotificationEvent
        {
            SessionId = "session-001",
            Type = (int)NotificationEventType.Answer,
            Details = details,
            TimeStamp = DateTime.UtcNow
        };

        // Act
        var result = storedEvent.GetTypedDetails();

        // Assert
        Assert.That(result, Is.Not.Null);
        Assert.That(result, Is.InstanceOf<AnswerDto>());
        var answerResult = result as AnswerDto;
        Assert.That(answerResult!.QuestionId, Is.EqualTo(answerDto.QuestionId));
        Assert.That(answerResult.IsCorrect, Is.EqualTo(answerDto.IsCorrect));
    }

    [Test]
    public void GetTypedDetails_ReturnsPurchaseConfirmationDto_WhenTypeIsPurchaseConfirmation()
    {
        // Arrange
        var purchaseDto = new QuizAttemptPurchaseConfirmationDto
        {
            PurchaseId = "purchase-789",
            SessionId = "session-001",
            Amount = 1500,
            Status = "Confirmed"
        };
        var details = JsonSerializer.Serialize(purchaseDto, new JsonSerializerOptions
        {
            PropertyNamingPolicy = JsonNamingPolicy.CamelCase
        });

        var storedEvent = new StoredNotificationEvent
        {
            SessionId = "session-001",
            Type = (int)NotificationEventType.PurchaseConfirmation,
            Details = details,
            TimeStamp = DateTime.UtcNow
        };

        // Act
        var result = storedEvent.GetTypedDetails();

        // Assert
        Assert.That(result, Is.Not.Null);
        Assert.That(result, Is.InstanceOf<QuizAttemptPurchaseConfirmationDto>());
        var purchaseResult = result as QuizAttemptPurchaseConfirmationDto;
        Assert.That(purchaseResult!.PurchaseId, Is.EqualTo(purchaseDto.PurchaseId));
        Assert.That(purchaseResult.Amount, Is.EqualTo(purchaseDto.Amount));
    }

    [Test]
    public void GetTypedDetails_ReturnsNull_WhenTypeIsUnknown()
    {
        // Arrange
        var storedEvent = new StoredNotificationEvent
        {
            SessionId = "session-001",
            Type = 999,
            Details = "{\"unknownField\":\"value\"}",
            TimeStamp = DateTime.UtcNow
        };

        // Act
        var result = storedEvent.GetTypedDetails();

        // Assert
        Assert.That(result, Is.Null);
    }

    [Test]
    public void FromNotificationEvent_CreatesStoredEventCorrectly()
    {
        // Arrange
        var timestamp = DateTime.UtcNow;
        var notificationEvent = new NotificationEvent
        {
            SessionId = "session-001",
            Type = (int)NotificationEventType.Answer,
            Details = "{\"questionId\":\"q1\",\"isCorrect\":true}",
            TimeStamp = timestamp
        };

        // Act
        var storedEvent = StoredNotificationEvent.FromNotificationEvent(notificationEvent);

        // Assert
        Assert.That(storedEvent, Is.Not.Null);
        Assert.That(storedEvent.SessionId, Is.EqualTo(notificationEvent.SessionId));
        Assert.That(storedEvent.Type, Is.EqualTo(notificationEvent.Type));
        Assert.That(storedEvent.Details, Is.EqualTo(notificationEvent.Details));
        Assert.That(storedEvent.TimeStamp, Is.EqualTo(notificationEvent.TimeStamp));
    }

    [Test]
    public void FromNotificationEvent_HandlesAnswerEvent()
    {
        // Arrange
        var timestamp = DateTime.UtcNow;
        var answerDto = new AnswerDto
        {
            QuestionId = "q-123",
            SelectedOptionId = "opt-456",
            IsCorrect = true
        };
        var details = JsonSerializer.Serialize(answerDto, new JsonSerializerOptions
        {
            PropertyNamingPolicy = JsonNamingPolicy.CamelCase
        });

        var notificationEvent = new NotificationEvent
        {
            SessionId = "session-002",
            Type = (int)NotificationEventType.Answer,
            Details = details,
            TimeStamp = timestamp
        };

        // Act
        var storedEvent = StoredNotificationEvent.FromNotificationEvent(notificationEvent);
        var retrievedAnswer = storedEvent.GetDetailsAs<AnswerDto>();

        // Assert
        Assert.That(retrievedAnswer, Is.Not.Null);
        Assert.That(retrievedAnswer!.QuestionId, Is.EqualTo(answerDto.QuestionId));
        Assert.That(retrievedAnswer.SelectedOptionId, Is.EqualTo(answerDto.SelectedOptionId));
        Assert.That(retrievedAnswer.IsCorrect, Is.EqualTo(answerDto.IsCorrect));
    }

    [Test]
    public void FromNotificationEvent_HandlesPurchaseConfirmationEvent()
    {
        // Arrange
        var timestamp = DateTime.UtcNow;
        var purchaseDto = new QuizAttemptPurchaseConfirmationDto
        {
            PurchaseId = "p-789",
            SessionId = "session-003",
            Amount = 3000,
            Status = "Confirmed"
        };
        var details = JsonSerializer.Serialize(purchaseDto, new JsonSerializerOptions
        {
            PropertyNamingPolicy = JsonNamingPolicy.CamelCase
        });

        var notificationEvent = new NotificationEvent
        {
            SessionId = "session-003",
            Type = (int)NotificationEventType.PurchaseConfirmation,
            Details = details,
            TimeStamp = timestamp
        };

        // Act
        var storedEvent = StoredNotificationEvent.FromNotificationEvent(notificationEvent);
        var retrievedPurchase = storedEvent.GetDetailsAs<QuizAttemptPurchaseConfirmationDto>();

        // Assert
        Assert.That(retrievedPurchase, Is.Not.Null);
        Assert.That(retrievedPurchase!.PurchaseId, Is.EqualTo(purchaseDto.PurchaseId));
        Assert.That(retrievedPurchase.Amount, Is.EqualTo(purchaseDto.Amount));
        Assert.That(retrievedPurchase.Status, Is.EqualTo(purchaseDto.Status));
    }
}
