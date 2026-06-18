using Dashboard.Data;
using Dashboard.Models;
using Dashboard.Repositories;
using Microsoft.EntityFrameworkCore;
using NUnit.Framework;

namespace Dashboard.Tests.Repositories;

/// <summary>
/// Unit tests for StoredNotificationEventRepository.
///
/// CCC (Convention, Configuration, Code) Testing Principles:
/// - We do NOT test log messages to avoid red tests when only log messages change
/// - Focus on testing repository logic and data access
/// - Logging is considered an implementation detail
/// </summary>
[TestFixture]
public class StoredNotificationEventRepositoryTests
{
    private DashboardDbContext _context = null!;
    private StoredNotificationEventRepository _repository = null!;

    [SetUp]
    public void SetUp()
    {
        var options = new DbContextOptionsBuilder<DashboardDbContext>()
            .UseInMemoryDatabase(databaseName: Guid.NewGuid().ToString())
            .Options;

        _context = new DashboardDbContext(options);
        _repository = new StoredNotificationEventRepository(_context);
    }

    [TearDown]
    public void TearDown()
    {
        _context.Database.EnsureDeleted();
        _context.Dispose();
    }

    [Test]
    public async Task GetEventsBySessionIdAsync_ReturnsAllEventsForSession()
    {
        // Arrange
        const string SESSION_ID = "test-session-001";
        var now = DateTime.UtcNow;

        var events = new[]
        {
            new StoredNotificationEvent
            {
                SessionId = SESSION_ID,
                Type = (int)NotificationEventType.Answer,
                Details = "{\"questionId\":\"q1\",\"isCorrect\":true}",
                TimeStamp = now.AddMinutes(-10)
            },
            new StoredNotificationEvent
            {
                SessionId = SESSION_ID,
                Type = (int)NotificationEventType.PurchaseConfirmation,
                Details = "{\"purchaseId\":\"p1\",\"amount\":100}",
                TimeStamp = now.AddMinutes(-5)
            },
            new StoredNotificationEvent
            {
                SessionId = "other-session",
                Type = (int)NotificationEventType.Answer,
                Details = "{\"questionId\":\"q2\",\"isCorrect\":false}",
                TimeStamp = now
            }
        };

        _context.StoredNotificationEvents.AddRange(events);
        await _context.SaveChangesAsync();

        // Act
        var result = await _repository.GetEventsBySessionIdAsync(SESSION_ID);

        // Assert
        Assert.That(result, Has.Count.EqualTo(2));
        Assert.That(result.All(e => e.SessionId == SESSION_ID), Is.True);
    }

    [Test]
    public async Task GetEventsBySessionIdAsync_ReturnsEventsOrderedByTimestamp()
    {
        // Arrange
        const string SESSION_ID = "test-session-001";
        var now = DateTime.UtcNow;

        var events = new[]
        {
            new StoredNotificationEvent
            {
                SessionId = SESSION_ID,
                Type = (int)NotificationEventType.Answer,
                Details = "{\"questionId\":\"q3\"}",
                TimeStamp = now.AddMinutes(5)
            },
            new StoredNotificationEvent
            {
                SessionId = SESSION_ID,
                Type = (int)NotificationEventType.Answer,
                Details = "{\"questionId\":\"q1\"}",
                TimeStamp = now.AddMinutes(-10)
            },
            new StoredNotificationEvent
            {
                SessionId = SESSION_ID,
                Type = (int)NotificationEventType.Answer,
                Details = "{\"questionId\":\"q2\"}",
                TimeStamp = now
            }
        };

        _context.StoredNotificationEvents.AddRange(events);
        await _context.SaveChangesAsync();

        // Act
        var result = await _repository.GetEventsBySessionIdAsync(SESSION_ID);

        // Assert
        Assert.That(result, Has.Count.EqualTo(3));
        Assert.That(result[0].TimeStamp, Is.LessThan(result[1].TimeStamp));
        Assert.That(result[1].TimeStamp, Is.LessThan(result[2].TimeStamp));
    }

    [Test]
    public async Task GetEventsBySessionIdAsync_ReturnsEmptyListWhenNoEvents()
    {
        // Arrange
        const string SESSION_ID = "non-existent-session";

        // Act
        var result = await _repository.GetEventsBySessionIdAsync(SESSION_ID);

        // Assert
        Assert.That(result, Is.Empty);
    }

    [Test]
    public async Task GetAnswerEventsBySessionIdAsync_ReturnsOnlyAnswerEvents()
    {
        // Arrange
        const string SESSION_ID = "test-session-001";
        var now = DateTime.UtcNow;

        var events = new[]
        {
            new StoredNotificationEvent
            {
                SessionId = SESSION_ID,
                Type = (int)NotificationEventType.Answer,
                Details = "{\"questionId\":\"q1\",\"isCorrect\":true}",
                TimeStamp = now.AddMinutes(-10)
            },
            new StoredNotificationEvent
            {
                SessionId = SESSION_ID,
                Type = (int)NotificationEventType.PurchaseConfirmation,
                Details = "{\"purchaseId\":\"p1\",\"amount\":100}",
                TimeStamp = now.AddMinutes(-5)
            },
            new StoredNotificationEvent
            {
                SessionId = SESSION_ID,
                Type = (int)NotificationEventType.Answer,
                Details = "{\"questionId\":\"q2\",\"isCorrect\":false}",
                TimeStamp = now
            }
        };

        _context.StoredNotificationEvents.AddRange(events);
        await _context.SaveChangesAsync();

        // Act
        var result = await _repository.GetAnswerEventsBySessionIdAsync(SESSION_ID);

        // Assert
        Assert.That(result, Has.Count.EqualTo(2));
        Assert.That(result.All(e => e.Type == (int)NotificationEventType.Answer), Is.True);
    }

    [Test]
    public async Task GetAnswerEventsBySessionIdAsync_ReturnsEventsOrderedByTimestamp()
    {
        // Arrange
        const string SESSION_ID = "test-session-001";
        var now = DateTime.UtcNow;

        var events = new[]
        {
            new StoredNotificationEvent
            {
                SessionId = SESSION_ID,
                Type = (int)NotificationEventType.Answer,
                Details = "{\"questionId\":\"q2\"}",
                TimeStamp = now
            },
            new StoredNotificationEvent
            {
                SessionId = SESSION_ID,
                Type = (int)NotificationEventType.Answer,
                Details = "{\"questionId\":\"q1\"}",
                TimeStamp = now.AddMinutes(-10)
            }
        };

        _context.StoredNotificationEvents.AddRange(events);
        await _context.SaveChangesAsync();

        // Act
        var result = await _repository.GetAnswerEventsBySessionIdAsync(SESSION_ID);

        // Assert
        Assert.That(result, Has.Count.EqualTo(2));
        Assert.That(result[0].TimeStamp, Is.LessThan(result[1].TimeStamp));
    }

    [Test]
    public async Task GetAnswerEventsBySessionIdAsync_ExcludesOtherSessions()
    {
        // Arrange
        const string SESSION_ID = "test-session-001";
        var now = DateTime.UtcNow;

        var events = new[]
        {
            new StoredNotificationEvent
            {
                SessionId = SESSION_ID,
                Type = (int)NotificationEventType.Answer,
                Details = "{\"questionId\":\"q1\"}",
                TimeStamp = now
            },
            new StoredNotificationEvent
            {
                SessionId = "other-session",
                Type = (int)NotificationEventType.Answer,
                Details = "{\"questionId\":\"q2\"}",
                TimeStamp = now.AddMinutes(5)
            }
        };

        _context.StoredNotificationEvents.AddRange(events);
        await _context.SaveChangesAsync();

        // Act
        var result = await _repository.GetAnswerEventsBySessionIdAsync(SESSION_ID);

        // Assert
        Assert.That(result, Has.Count.EqualTo(1));
        Assert.That(result[0].SessionId, Is.EqualTo(SESSION_ID));
    }

    [Test]
    public async Task GetAnswerEventsBySessionIdAsync_ReturnsEmptyListWhenNoAnswerEvents()
    {
        // Arrange
        const string SESSION_ID = "test-session-001";
        var now = DateTime.UtcNow;

        var events = new[]
        {
            new StoredNotificationEvent
            {
                SessionId = SESSION_ID,
                Type = (int)NotificationEventType.PurchaseConfirmation,
                Details = "{\"purchaseId\":\"p1\"}",
                TimeStamp = now
            }
        };

        _context.StoredNotificationEvents.AddRange(events);
        await _context.SaveChangesAsync();

        // Act
        var result = await _repository.GetAnswerEventsBySessionIdAsync(SESSION_ID);

        // Assert
        Assert.That(result, Is.Empty);
    }
}
