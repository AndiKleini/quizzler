using Dashboard.Data;
using Dashboard.Models;
using Dashboard.Services;
using Microsoft.EntityFrameworkCore;
using Microsoft.Extensions.Logging;
using Moq;
using NUnit.Framework;

namespace Dashboard.Tests.Services;

/// <summary>
/// Unit tests for StreamNotificationEventHandlerService.
///
/// CCC (Convention, Configuration, Code) Testing Principles:
/// - We do NOT test log messages to avoid red tests when only log messages change
/// - Focus on testing business logic and data integrity
/// - Logging is considered an implementation detail
/// </summary>
[TestFixture]
public class StreamNotificationEventHandlerServiceTests
{
    private DashboardDbContext _context = null!;
    private Mock<ILogger<StreamNotificationEventHandlerService>> _mockLogger = null!;
    private StreamNotificationEventHandlerService _service = null!;

    [SetUp]
    public void SetUp()
    {
        var options = new DbContextOptionsBuilder<DashboardDbContext>()
            .UseInMemoryDatabase(databaseName: Guid.NewGuid().ToString())
            .Options;

        _context = new DashboardDbContext(options);
        _mockLogger = new Mock<ILogger<StreamNotificationEventHandlerService>>();
        _service = new StreamNotificationEventHandlerService(_mockLogger.Object, _context);
    }

    [TearDown]
    public void TearDown()
    {
        _context.Database.EnsureDeleted();
        _context.Dispose();
    }

    [Test]
    public async Task HandleNotificationEventAsync_StoresAnswerEventSuccessfully()
    {
        // Arrange
        var notificationEvent = new NotificationEvent
        {
            SessionId = "test-session-001",
            Type = (int)NotificationEventType.Answer,
            Details = "{\"questionId\":\"q-123\",\"selectedOptionId\":\"opt-456\",\"isCorrect\":true}",
            TimeStamp = DateTime.UtcNow
        };

        // Act
        await _service.HandleNotificationEventAsync(notificationEvent);

        // Assert
        var storedEvents = await _context.StoredNotificationEvents.ToListAsync();
        Assert.That(storedEvents, Has.Count.EqualTo(1));
        Assert.That(storedEvents[0].SessionId, Is.EqualTo(notificationEvent.SessionId));
        Assert.That(storedEvents[0].Type, Is.EqualTo(notificationEvent.Type));
        Assert.That(storedEvents[0].Details, Is.EqualTo(notificationEvent.Details));
        Assert.That(storedEvents[0].TimeStamp, Is.EqualTo(notificationEvent.TimeStamp));
    }

    [Test]
    public async Task HandleNotificationEventAsync_StoresPurchaseConfirmationEventSuccessfully()
    {
        // Arrange
        var notificationEvent = new NotificationEvent
        {
            SessionId = "test-session-002",
            Type = (int)NotificationEventType.PurchaseConfirmation,
            Details = "{\"purchaseId\":\"p-789\",\"sessionId\":\"test-session-002\",\"amount\":2500,\"status\":\"Confirmed\"}",
            TimeStamp = DateTime.UtcNow
        };

        // Act
        await _service.HandleNotificationEventAsync(notificationEvent);

        // Assert
        var storedEvents = await _context.StoredNotificationEvents.ToListAsync();
        Assert.That(storedEvents, Has.Count.EqualTo(1));
        Assert.That(storedEvents[0].SessionId, Is.EqualTo(notificationEvent.SessionId));
        Assert.That(storedEvents[0].Type, Is.EqualTo(notificationEvent.Type));
        Assert.That(storedEvents[0].Details, Is.EqualTo(notificationEvent.Details));
    }

    [Test]
    public async Task HandleNotificationEventAsync_AssignsIdToStoredEvent()
    {
        // Arrange
        var notificationEvent = new NotificationEvent
        {
            SessionId = "test-session-003",
            Type = (int)NotificationEventType.Answer,
            Details = "{\"questionId\":\"q-1\"}",
            TimeStamp = DateTime.UtcNow
        };

        // Act
        await _service.HandleNotificationEventAsync(notificationEvent);

        // Assert
        var storedEvents = await _context.StoredNotificationEvents.ToListAsync();
        Assert.That(storedEvents[0].Id, Is.GreaterThan(0));
    }

    [Test]
    public async Task HandleNotificationEventAsync_StoresMultipleEventsForSameSession()
    {
        // Arrange
        const string SESSION_ID = "test-session-004";
        var event1 = new NotificationEvent
        {
            SessionId = SESSION_ID,
            Type = (int)NotificationEventType.Answer,
            Details = "{\"questionId\":\"q-1\",\"isCorrect\":true}",
            TimeStamp = DateTime.UtcNow.AddMinutes(-10)
        };
        var event2 = new NotificationEvent
        {
            SessionId = SESSION_ID,
            Type = (int)NotificationEventType.Answer,
            Details = "{\"questionId\":\"q-2\",\"isCorrect\":false}",
            TimeStamp = DateTime.UtcNow.AddMinutes(-5)
        };
        var event3 = new NotificationEvent
        {
            SessionId = SESSION_ID,
            Type = (int)NotificationEventType.PurchaseConfirmation,
            Details = "{\"purchaseId\":\"p-1\",\"amount\":1000}",
            TimeStamp = DateTime.UtcNow
        };

        // Act
        await _service.HandleNotificationEventAsync(event1);
        await _service.HandleNotificationEventAsync(event2);
        await _service.HandleNotificationEventAsync(event3);

        // Assert
        var storedEvents = await _context.StoredNotificationEvents
            .Where(e => e.SessionId == SESSION_ID)
            .ToListAsync();
        Assert.That(storedEvents, Has.Count.EqualTo(3));
    }

    [Test]
    public async Task HandleNotificationEventAsync_StoresEventsSeparatelyForDifferentSessions()
    {
        // Arrange
        var event1 = new NotificationEvent
        {
            SessionId = "session-001",
            Type = (int)NotificationEventType.Answer,
            Details = "{\"questionId\":\"q-1\"}",
            TimeStamp = DateTime.UtcNow
        };
        var event2 = new NotificationEvent
        {
            SessionId = "session-002",
            Type = (int)NotificationEventType.Answer,
            Details = "{\"questionId\":\"q-2\"}",
            TimeStamp = DateTime.UtcNow
        };

        // Act
        await _service.HandleNotificationEventAsync(event1);
        await _service.HandleNotificationEventAsync(event2);

        // Assert
        var session1Events = await _context.StoredNotificationEvents
            .Where(e => e.SessionId == "session-001")
            .ToListAsync();
        var session2Events = await _context.StoredNotificationEvents
            .Where(e => e.SessionId == "session-002")
            .ToListAsync();

        Assert.That(session1Events, Has.Count.EqualTo(1));
        Assert.That(session2Events, Has.Count.EqualTo(1));
    }

    [Test]
    public async Task HandleNotificationEventAsync_PreservesTimestampPrecision()
    {
        // Arrange
        var timestamp = new DateTime(2026, 6, 18, 14, 30, 45, 123, DateTimeKind.Utc);
        var notificationEvent = new NotificationEvent
        {
            SessionId = "test-session-008",
            Type = (int)NotificationEventType.Answer,
            Details = "{\"questionId\":\"q-1\"}",
            TimeStamp = timestamp
        };

        // Act
        await _service.HandleNotificationEventAsync(notificationEvent);

        // Assert
        var storedEvent = await _context.StoredNotificationEvents.FirstAsync();
        Assert.That(storedEvent.TimeStamp, Is.EqualTo(timestamp));
    }

    [Test]
    public async Task HandleNotificationEventAsync_StoresDetailsAsIs()
    {
        // Arrange
        const string DETAILS = "{\"questionId\":\"q-123\",\"selectedOptionId\":\"opt-456\",\"isCorrect\":true,\"metadata\":{\"source\":\"mobile\"}}";
        var notificationEvent = new NotificationEvent
        {
            SessionId = "test-session-009",
            Type = (int)NotificationEventType.Answer,
            Details = DETAILS,
            TimeStamp = DateTime.UtcNow
        };

        // Act
        await _service.HandleNotificationEventAsync(notificationEvent);

        // Assert
        var storedEvent = await _context.StoredNotificationEvents.FirstAsync();
        Assert.That(storedEvent.Details, Is.EqualTo(DETAILS));
    }

    [Test]
    public async Task HandleNotificationEventAsync_ThrowsAndLogsOnDatabaseError()
    {
        // Arrange - Create a separate disposed context for this test
        var options = new DbContextOptionsBuilder<DashboardDbContext>()
            .UseInMemoryDatabase(databaseName: Guid.NewGuid().ToString())
            .Options;
        var disposedContext = new DashboardDbContext(options);
        disposedContext.Dispose();

        var serviceWithDisposedContext = new StreamNotificationEventHandlerService(_mockLogger.Object, disposedContext);

        var notificationEvent = new NotificationEvent
        {
            SessionId = "test-session-010",
            Type = (int)NotificationEventType.Answer,
            Details = "{\"questionId\":\"q-1\"}",
            TimeStamp = DateTime.UtcNow
        };

        // Act & Assert
        Assert.ThrowsAsync<ObjectDisposedException>(async () =>
            await serviceWithDisposedContext.HandleNotificationEventAsync(notificationEvent));
    }

    [Test]
    public async Task HandleNotificationEventAsync_HandlesEmptyDetailsString()
    {
        // Arrange
        var notificationEvent = new NotificationEvent
        {
            SessionId = "test-session-011",
            Type = (int)NotificationEventType.Answer,
            Details = "",
            TimeStamp = DateTime.UtcNow
        };

        // Act
        await _service.HandleNotificationEventAsync(notificationEvent);

        // Assert
        var storedEvent = await _context.StoredNotificationEvents.FirstAsync();
        Assert.That(storedEvent.Details, Is.EqualTo(""));
    }

    [Test]
    public async Task HandleNotificationEventAsync_HandlesLargeDetailsString()
    {
        // Arrange
        var largeDetails = new string('x', 10000);
        var notificationEvent = new NotificationEvent
        {
            SessionId = "test-session-012",
            Type = (int)NotificationEventType.Answer,
            Details = largeDetails,
            TimeStamp = DateTime.UtcNow
        };

        // Act
        await _service.HandleNotificationEventAsync(notificationEvent);

        // Assert
        var storedEvent = await _context.StoredNotificationEvents.FirstAsync();
        Assert.That(storedEvent.Details, Has.Length.EqualTo(10000));
        Assert.That(storedEvent.Details, Is.EqualTo(largeDetails));
    }

    [Test]
    public async Task HandleNotificationEventAsync_PreservesEventTypeCorrectly()
    {
        // Arrange
        var answerEvent = new NotificationEvent
        {
            SessionId = "test-session-013",
            Type = (int)NotificationEventType.Answer,
            Details = "{\"questionId\":\"q-1\"}",
            TimeStamp = DateTime.UtcNow.AddMinutes(-5)
        };
        var purchaseEvent = new NotificationEvent
        {
            SessionId = "test-session-013",
            Type = (int)NotificationEventType.PurchaseConfirmation,
            Details = "{\"purchaseId\":\"p-1\"}",
            TimeStamp = DateTime.UtcNow
        };

        // Act
        await _service.HandleNotificationEventAsync(answerEvent);
        await _service.HandleNotificationEventAsync(purchaseEvent);

        // Assert
        var storedEvents = await _context.StoredNotificationEvents
            .OrderBy(e => e.TimeStamp)
            .ToListAsync();

        Assert.That(storedEvents[0].Type, Is.EqualTo((int)NotificationEventType.Answer));
        Assert.That(storedEvents[1].Type, Is.EqualTo((int)NotificationEventType.PurchaseConfirmation));
    }
}
