using Dashboard.Controllers;
using Dashboard.Models;
using Dashboard.Repositories;
using Microsoft.AspNetCore.Mvc;
using Microsoft.Extensions.Logging;
using Moq;
using NUnit.Framework;
using Shouldly;

namespace Dashboard.Tests.Controllers;

/// <summary>
/// Unit tests for SessionDashboardController.
///
/// CCC (Convention, Configuration, Code) Testing Principles:
/// - We do NOT test log messages to avoid red tests when only log messages change
/// - Focus on testing HTTP responses and controller behavior
/// - Logging is considered an implementation detail
/// </summary>
[TestFixture]
public class SessionDashboardControllerTests
{
    private Mock<ISessionDashboardRepository> _mockRepository = null!;
    private Mock<IStoredNotificationEventRepository> _mockEventRepository = null!;
    private Mock<ILogger<SessionDashboardController>> _mockLogger = null!;
    private SessionDashboardController _controller = null!;

    [SetUp]
    public void SetUp()
    {
        _mockRepository = new Mock<ISessionDashboardRepository>();
        _mockEventRepository = new Mock<IStoredNotificationEventRepository>();
        _mockLogger = new Mock<ILogger<SessionDashboardController>>();
        _controller = new SessionDashboardController(_mockRepository.Object, _mockEventRepository.Object, _mockLogger.Object);
    }

    [Test]
    public async Task GetDashboard_WhenDataExists_ReturnsOkWithData()
    {
        // Arrange
        var dashboardData = new SessionDashboardData
        {
            Id = 1,
            DashboardId = "SomeDashboardId",
            PaymentAmount = 500,
            NumberOfPayments = 5,
            WrongAnswers = 3,
            CorrectAnswers = 7,
            Questions = 10
        };
        _mockRepository
            .Setup(repo => repo.GetDashboardDataAsync())
            .ReturnsAsync(dashboardData);

        // Act
        var result = await _controller.GetDashboard();

        // Assert
        Assert.That(result.Result, Is.TypeOf<OkObjectResult>());
        var okResult = result.Result as OkObjectResult;
        Assert.That(okResult, Is.Not.Null);
        Assert.That(okResult!.Value, Is.EqualTo(dashboardData));
    }

    [Test]
    public async Task GetDashboardByDashboardId_WhenDataExists_ReturnsOkWithData()
    {
    // Arrange
        const string DASHBOARD_ID = "SomeDashboardId";
        var dashboardData = new SessionDashboardData
        {
            Id = 1,
            DashboardId = DASHBOARD_ID,
            PaymentAmount = 500,
            NumberOfPayments = 5,
            WrongAnswers = 3,
            CorrectAnswers = 7,
            Questions = 10
        };
        _mockRepository
            .Setup(repo => repo.GetDashboardDataByDashboardIdAsync(DASHBOARD_ID))
            .ReturnsAsync(
                new SessionDashboardData()
                {
                    Id = dashboardData.Id,
                    DashboardId = dashboardData.DashboardId,
                    PaymentAmount = dashboardData.PaymentAmount,
                    NumberOfPayments = dashboardData.NumberOfPayments,
                    WrongAnswers = dashboardData.WrongAnswers,
                    CorrectAnswers = dashboardData.CorrectAnswers,
                    Questions = dashboardData.Questions
                });

        _mockEventRepository
            .Setup(repo => repo.GetAnswerEventsBySessionIdAsync(DASHBOARD_ID))
            .ReturnsAsync(new List<StoredNotificationEvent>());

        // Act
        var result = await _controller.GetDashboardById(DASHBOARD_ID);

        // Assert
        Assert.That(result.Result, Is.TypeOf<OkObjectResult>());
        var okResult = result.Result as OkObjectResult;
        Assert.That(okResult, Is.Not.Null);
        var returnedData = okResult!.Value as SessionDashboardData;
        Assert.That(returnedData, Is.Not.Null);
        Assert.That(returnedData!.Id, Is.EqualTo(dashboardData.Id));
        Assert.That(returnedData.DashboardId, Is.EqualTo(dashboardData.DashboardId));
        Assert.That(returnedData.PaymentAmount, Is.EqualTo(dashboardData.PaymentAmount));
        Assert.That(returnedData.NumberOfPayments, Is.EqualTo(dashboardData.NumberOfPayments));
        Assert.That(returnedData.WrongAnswers, Is.EqualTo(dashboardData.WrongAnswers));
        Assert.That(returnedData.CorrectAnswers, Is.EqualTo(dashboardData.CorrectAnswers));
        Assert.That(returnedData.Questions, Is.EqualTo(dashboardData.Questions));
        Assert.That(returnedData.Answers, Is.Not.Null);
        Assert.That(returnedData.Answers, Is.Empty);
    }

    [Test]
    public async Task GetDashboardByDashboardId_WhenNoDataExists_ReturnsNotFound()
    {
        const string DASHBOARD_ID = "SomeDashboardId";
        // Arrange
        _mockRepository
            .Setup(repo => repo.GetDashboardDataByDashboardIdAsync(DASHBOARD_ID))
            .ReturnsAsync((SessionDashboardData?)null);

        _mockEventRepository
            .Setup(repo => repo.GetAnswerEventsBySessionIdAsync(DASHBOARD_ID))
            .ReturnsAsync(new List<StoredNotificationEvent>());

        // Act
        var result = await _controller.GetDashboardById(DASHBOARD_ID);

        // Assert
        Assert.That(result.Result, Is.TypeOf<NotFoundResult>());
    }

    [Test]
    public async Task GetDashboard_WhenNoDataExists_ReturnsNotFound()
    {
        // Arrange
        _mockRepository
            .Setup(repo => repo.GetDashboardDataAsync())
            .ReturnsAsync((SessionDashboardData?)null);

        // Act
        var result = await _controller.GetDashboard();

        // Assert
        Assert.That(result.Result, Is.TypeOf<NotFoundResult>());
    }

    [Test]
    public async Task GetDashboard_CallsRepositoryOnce()
    {
        // Arrange
        var dashboardData = new SessionDashboardData
        {
            Id = 1,
            DashboardId = "SomeDashboardId",
            PaymentAmount = 500,
            NumberOfPayments = 5,
            WrongAnswers = 3,
            CorrectAnswers = 7,
            Questions = 10
        };
        _mockRepository
            .Setup(repo => repo.GetDashboardDataAsync())
            .ReturnsAsync(dashboardData);

        // Act
        await _controller.GetDashboard();

        // Assert
        _mockRepository.Verify(repo => repo.GetDashboardDataAsync(), Times.Once);
    }

    [Test]
    public async Task GetDashboard_ReturnsCorrectDataStructure()
    {
        // Arrange
        var dashboardData = new SessionDashboardData
        {
            Id = 1,
            DashboardId = "SomeDashboardId",
            PaymentAmount = 25000,
            NumberOfPayments = 100,
            WrongAnswers = 30,
            CorrectAnswers = 70,
            Questions = 100
        };
        _mockRepository
            .Setup(repo => repo.GetDashboardDataAsync())
            .ReturnsAsync(
                new SessionDashboardData() 
                {
                    Id = dashboardData.Id,
                    DashboardId = dashboardData.DashboardId,
                    PaymentAmount = dashboardData.PaymentAmount,
                    NumberOfPayments = dashboardData.NumberOfPayments,
                    WrongAnswers = dashboardData.WrongAnswers,
                    CorrectAnswers = dashboardData.CorrectAnswers,
                    Questions = dashboardData.Questions
                });

        // Act
        var result = await _controller.GetDashboard();

        // Assert
        var okResult = result.Result as OkObjectResult;
        var returnedData = okResult!.Value as SessionDashboardData;

        returnedData.ShouldBeEquivalentTo(dashboardData);
    }
}