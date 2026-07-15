using Dashboard.Controllers;
using Dashboard.Models;
using Dashboard.Repositories;
using Microsoft.AspNetCore.Http.HttpResults;
using Microsoft.AspNetCore.Mvc;
using Microsoft.Extensions.Logging;
using Moq;
using NUnit.Framework;
using Shouldly;

namespace Dashboard.Tests.Controllers;

[TestFixture]
public class SessionDashboardControllerTests
{
    private Mock<ISessionDashboardRepository> _mockRepository = null!;
    private Mock<ILogger<SessionDashboardController>> _mockLogger = null!;
    private SessionDashboardController _controller = null!;

    [SetUp]
    public void SetUp()
    {
        _mockRepository = new Mock<ISessionDashboardRepository>();
        _mockLogger = new Mock<ILogger<SessionDashboardController>>();
        _controller = new SessionDashboardController(_mockRepository.Object, _mockLogger.Object);
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

        // Act
        var result = await _controller.GetDashboardById(DASHBOARD_ID);

        // Assert
        Assert.That(result.Result, Is.TypeOf<OkObjectResult>());
        var okResult = result.Result as OkObjectResult;
        Assert.That(okResult, Is.Not.Null);
        okResult!.Value.ShouldBeEquivalentTo(dashboardData);
    }

    [Test]
    public async Task GetDashboardByDashboardId_WhenNoDataExists_ReturnsServerError()
    {
        const string DASHBOARD_ID = "SomeDashboardId";
        // Arrange
        _mockRepository
            .Setup(repo => repo.GetDashboardDataByDashboardIdAsync(DASHBOARD_ID))
            .ReturnsAsync((SessionDashboardData?)null);

        // Act
        var result = await _controller.GetDashboardById(DASHBOARD_ID);

        // Assert
        Assert.That(result.Result, Is.TypeOf<StatusCodeResult>());
    }

    [Test]
    public async Task GetDashboardByDashboardId_WhenDashboradIdIsInvalid_ReturnsServerError()
    {
        const string INVALID_DASHBOARD_ID = "1";
        // Arrange
        _mockRepository
            .Setup(repo => repo.GetDashboardDataByDashboardIdAsync(INVALID_DASHBOARD_ID))
            .ReturnsAsync((SessionDashboardData?)null);

        // Act
        var result = await _controller.GetDashboardById(INVALID_DASHBOARD_ID);

        // Assert
        Assert.That(result.Result, Is.TypeOf<StatusCodeResult>());
        Assert.That((result.Result as StatusCodeResult)?.StatusCode, Is.EqualTo(500));
    }

    [Test]
    public async Task GetDashboard_WhenNoDataExists_ReturnsServerError()
    {
        // Arrange
        _mockRepository
            .Setup(repo => repo.GetDashboardDataAsync())
            .ReturnsAsync((SessionDashboardData?)null);

        // Act
        var result = await _controller.GetDashboard();

        // Assert
        Assert.That(result.Result, Is.TypeOf<StatusCodeResult>());
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