using Dashboard.Data;
using Dashboard.Models;
using Dashboard.Repositories;
using Dashboard.Services;
using Microsoft.EntityFrameworkCore;
using Microsoft.Extensions.DependencyInjection;
using Microsoft.Extensions.Logging;
using Moq;
using NUnit.Framework;

namespace Dashboard.Tests.Services;

/// <summary>
/// Unit tests for NotificationEventHandlerServiceFactory.
///
/// CCC (Convention, Configuration, Code) Testing Principles:
/// - We do NOT test log messages to avoid red tests when only log messages change
/// - Focus on testing business logic and handler selection
/// - Logging is considered an implementation detail
/// </summary>
[TestFixture]
public class NotificationEventHandlerServiceFactoryTests
{
    private Mock<ISessionDashboardRepository> _mockRepository = null!;
    private Mock<ILogger<NotificationEventHandlerServiceFactory>> _mockFactoryLogger = null!;
    private Mock<ILogger<NotificationEventHandlerService>> _mockHandlerLogger = null!;
    private Mock<ILogger<StreamNotificationEventHandlerService>> _mockStreamHandlerLogger = null!;
    private IServiceProvider _serviceProvider = null!;
    private NotificationEventHandlerServiceFactory _factory = null!;

    [SetUp]
    public void SetUp()
    {
        _mockRepository = new Mock<ISessionDashboardRepository>();
        _mockFactoryLogger = new Mock<ILogger<NotificationEventHandlerServiceFactory>>();
        _mockHandlerLogger = new Mock<ILogger<NotificationEventHandlerService>>();
        _mockStreamHandlerLogger = new Mock<ILogger<StreamNotificationEventHandlerService>>();

        var services = new ServiceCollection();

        // Add DbContext with in-memory database
        services.AddDbContext<DashboardDbContext>(options =>
            options.UseInMemoryDatabase(databaseName: Guid.NewGuid().ToString()));

        services.AddSingleton(_mockRepository.Object);
        services.AddSingleton(_mockFactoryLogger.Object);
        services.AddSingleton(_mockHandlerLogger.Object);
        services.AddSingleton(_mockStreamHandlerLogger.Object);
        services.AddScoped<ISessionDashboardRepository>(sp => _mockRepository.Object);
        services.AddScoped<ILogger<NotificationEventHandlerService>>(sp => _mockHandlerLogger.Object);
        services.AddScoped<ILogger<StreamNotificationEventHandlerService>>(sp => _mockStreamHandlerLogger.Object);

        _serviceProvider = services.BuildServiceProvider();
        _factory = new NotificationEventHandlerServiceFactory(_serviceProvider, _mockFactoryLogger.Object);
    }

    [Test]
    public async Task CreateHandlerAsync_WhenSessionExists_ReturnsNotificationEventHandlerService()
    {
        // Arrange
        const string SESSION_ID = "existing-session";
        var existingSession = new SessionDashboardData
        {
            Id = 1,
            DashboardId = SESSION_ID,
            PaymentAmount = 100,
            NumberOfPayments = 1,
            WrongAnswers = 0,
            CorrectAnswers = 5,
            Questions = 5
        };

        _mockRepository
            .Setup(repo => repo.GetDashboardDataByDashboardIdAsync(SESSION_ID))
            .ReturnsAsync(existingSession);

        // Act
        var handler = await _factory.CreateHandlerAsync(SESSION_ID);

        // Assert
        Assert.That(handler, Is.Not.Null);
        Assert.That(handler, Is.InstanceOf<NotificationEventHandlerService>());
        _mockRepository.Verify(repo => repo.GetDashboardDataByDashboardIdAsync(SESSION_ID), Times.Once);
    }

    [Test]
    public async Task CreateHandlerAsync_WhenSessionDoesNotExist_ReturnsStreamNotificationEventHandlerService()
    {
        // Arrange
        const string SESSION_ID = "new-session";

        _mockRepository
            .Setup(repo => repo.GetDashboardDataByDashboardIdAsync(SESSION_ID))
            .ReturnsAsync((SessionDashboardData?)null);

        // Act
        var handler = await _factory.CreateHandlerAsync(SESSION_ID);

        // Assert
        Assert.That(handler, Is.Not.Null);
        Assert.That(handler, Is.InstanceOf<StreamNotificationEventHandlerService>());
        _mockRepository.Verify(repo => repo.GetDashboardDataByDashboardIdAsync(SESSION_ID), Times.Once);
    }

    [Test]
    public async Task CreateHandlerAsync_ConsistentlyReturnsCorrectHandlerType()
    {
        // Arrange
        const string EXISTING_SESSION = "existing-session";
        const string NEW_SESSION = "new-session";

        var existingSessionData = new SessionDashboardData
        {
            Id = 1,
            DashboardId = EXISTING_SESSION,
            PaymentAmount = 100,
            NumberOfPayments = 1,
            WrongAnswers = 0,
            CorrectAnswers = 5,
            Questions = 5
        };

        _mockRepository
            .Setup(repo => repo.GetDashboardDataByDashboardIdAsync(EXISTING_SESSION))
            .ReturnsAsync(existingSessionData);

        _mockRepository
            .Setup(repo => repo.GetDashboardDataByDashboardIdAsync(NEW_SESSION))
            .ReturnsAsync((SessionDashboardData?)null);

        // Act
        var handler1 = await _factory.CreateHandlerAsync(EXISTING_SESSION);
        var handler2 = await _factory.CreateHandlerAsync(NEW_SESSION);
        var handler3 = await _factory.CreateHandlerAsync(EXISTING_SESSION);

        // Assert
        Assert.That(handler1, Is.InstanceOf<NotificationEventHandlerService>());
        Assert.That(handler2, Is.InstanceOf<StreamNotificationEventHandlerService>());
        Assert.That(handler3, Is.InstanceOf<NotificationEventHandlerService>());
    }

    [Test]
    public async Task CreateHandlerAsync_CallsRepositoryForEachInvocation()
    {
        // Arrange
        const string SESSION_ID = "test-session";

        _mockRepository
            .Setup(repo => repo.GetDashboardDataByDashboardIdAsync(SESSION_ID))
            .ReturnsAsync((SessionDashboardData?)null);

        // Act
        await _factory.CreateHandlerAsync(SESSION_ID);
        await _factory.CreateHandlerAsync(SESSION_ID);
        await _factory.CreateHandlerAsync(SESSION_ID);

        // Assert
        _mockRepository.Verify(repo => repo.GetDashboardDataByDashboardIdAsync(SESSION_ID), Times.Exactly(3));
    }

    [Test]
    public async Task CreateHandlerAsync_HandlesDifferentSessionIds()
    {
        // Arrange
        const string SESSION_1 = "session-001";
        const string SESSION_2 = "session-002";
        const string SESSION_3 = "session-003";

        var session1Data = new SessionDashboardData
        {
            Id = 1,
            DashboardId = SESSION_1,
            PaymentAmount = 100,
            NumberOfPayments = 1,
            WrongAnswers = 0,
            CorrectAnswers = 5,
            Questions = 5
        };

        _mockRepository
            .Setup(repo => repo.GetDashboardDataByDashboardIdAsync(SESSION_1))
            .ReturnsAsync(session1Data);

        _mockRepository
            .Setup(repo => repo.GetDashboardDataByDashboardIdAsync(SESSION_2))
            .ReturnsAsync((SessionDashboardData?)null);

        _mockRepository
            .Setup(repo => repo.GetDashboardDataByDashboardIdAsync(SESSION_3))
            .ReturnsAsync((SessionDashboardData?)null);

        // Act
        var handler1 = await _factory.CreateHandlerAsync(SESSION_1);
        var handler2 = await _factory.CreateHandlerAsync(SESSION_2);
        var handler3 = await _factory.CreateHandlerAsync(SESSION_3);

        // Assert
        Assert.That(handler1, Is.InstanceOf<NotificationEventHandlerService>());
        Assert.That(handler2, Is.InstanceOf<StreamNotificationEventHandlerService>());
        Assert.That(handler3, Is.InstanceOf<StreamNotificationEventHandlerService>());

        _mockRepository.Verify(repo => repo.GetDashboardDataByDashboardIdAsync(SESSION_1), Times.Once);
        _mockRepository.Verify(repo => repo.GetDashboardDataByDashboardIdAsync(SESSION_2), Times.Once);
        _mockRepository.Verify(repo => repo.GetDashboardDataByDashboardIdAsync(SESSION_3), Times.Once);
    }
}
