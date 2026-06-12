using Dashboard.Controllers;
using Dashboard.Models;
using Dashboard.Repositories;
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
