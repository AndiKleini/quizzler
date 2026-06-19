using Dashboard.Data;
using Dashboard.Models;
using Dashboard.Repositories;
using Microsoft.EntityFrameworkCore;
using NUnit.Framework;
using Shouldly;

namespace Dashboard.Tests.Repositories;

[TestFixture]
public class SessionDashboardRepositoryTests
{
    private DashboardDbContext _context = null!;
    private SessionDashboardRepository _repository = null!;

    [SetUp]
    public void SetUp()
    {
        var options = new DbContextOptionsBuilder<DashboardDbContext>()
            .UseInMemoryDatabase(databaseName: Guid.NewGuid().ToString())
            .Options;

        _context = new DashboardDbContext(options);
        _repository = new SessionDashboardRepository(_context);
    }

    [TearDown]
    public void TearDown()
    {
        _context.Database.EnsureDeleted();
        _context.Dispose();
    }

    [Test]
    public async Task GetDashboardDataAsync_WhenDataExists_ReturnsData()
    {
        // Arrange
        var dashboardData = new SessionDashboardData
        {
            Id = 1,
            DashboardId = "SomeDashBoardId",
            PaymentAmount = 500,
            NumberOfPayments = 5,
            WrongAnswers = 3,
            CorrectAnswers = 7,
            Questions = 10
        };
        _context.SessionDashboardData.Add(
            new SessionDashboardData
            {
                Id = dashboardData.Id,
                DashboardId = dashboardData.DashboardId,
                PaymentAmount = dashboardData.PaymentAmount,
                NumberOfPayments = dashboardData.NumberOfPayments,
                WrongAnswers = dashboardData.WrongAnswers,
                CorrectAnswers = dashboardData.CorrectAnswers,
                Questions = dashboardData.Questions
            });
        await _context.SaveChangesAsync();

        // Act
        var result = await _repository.GetDashboardDataAsync();

        // Assert
        result.ShouldNotBeNull();
        result.Id.ShouldBe(dashboardData.Id);
        result.DashboardId.ShouldBe(dashboardData.DashboardId);
        result.PaymentAmount.ShouldBe(dashboardData.PaymentAmount);
        result.NumberOfPayments.ShouldBe(dashboardData.NumberOfPayments);
        result.WrongAnswers.ShouldBe(dashboardData.WrongAnswers);
        result.CorrectAnswers.ShouldBe(dashboardData.CorrectAnswers);
        result.Questions.ShouldBe(dashboardData.Questions);
        result.Answers.ShouldNotBeNull();
        result.Answers.ShouldBeEmpty();
    }

     [Test]
    public async Task GetDashboardDataByDashoardIdAsync_WhenDataExists_ReturnsData()
    {
        // Arrange
        const string DASHBOARD_ID = "MyDashboardId";
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
        _context.SessionDashboardData.Add(
            new SessionDashboardData
            {
                Id = dashboardData.Id,
                DashboardId = dashboardData.DashboardId,
                PaymentAmount = dashboardData.PaymentAmount,
                NumberOfPayments = dashboardData.NumberOfPayments,
                WrongAnswers = dashboardData.WrongAnswers,
                CorrectAnswers = dashboardData.CorrectAnswers,
                Questions = dashboardData.Questions
            });
        await _context.SaveChangesAsync();

        // Act
        var result = await _repository.GetDashboardDataByDashboardIdAsync(DASHBOARD_ID);

        // Assert
        result.ShouldNotBeNull();
        result.Id.ShouldBe(dashboardData.Id);
        result.DashboardId.ShouldBe(dashboardData.DashboardId);
        result.PaymentAmount.ShouldBe(dashboardData.PaymentAmount);
        result.NumberOfPayments.ShouldBe(dashboardData.NumberOfPayments);
        result.WrongAnswers.ShouldBe(dashboardData.WrongAnswers);
        result.CorrectAnswers.ShouldBe(dashboardData.CorrectAnswers);
        result.Questions.ShouldBe(dashboardData.Questions);
        result.Answers.ShouldNotBeNull();
        result.Answers.ShouldBeEmpty();
    }

    [Test]
    public async Task GetDashboardDataAsync_WhenNoDataExists_ReturnsNull()
    {
        // Act
        var result = await _repository.GetDashboardDataAsync();

        // Assert
        result.ShouldBeNull();
    }

    [Test]
    public async Task GetDashboardDataAsync_WhenMultipleRecordsExist_ReturnsFirstRecord()
    {
        // Arrange
        var dashboardData1 = new SessionDashboardData
        {
            Id = 1,
            DashboardId = "SomeDashboardId",
            PaymentAmount = 500,
            NumberOfPayments = 5,
            WrongAnswers = 3,
            CorrectAnswers = 7,
            Questions = 10
        };
        var dashboardData2 = new SessionDashboardData
        {
            Id = 2,
            DashboardId = "SomeDashboardId",
            PaymentAmount = 1000,
            NumberOfPayments = 10,
            WrongAnswers = 5,
            CorrectAnswers = 15,
            Questions = 20
        };
        _context.SessionDashboardData.Add(dashboardData1);
        _context.SessionDashboardData.Add(dashboardData2);
        await _context.SaveChangesAsync();

        // Act
        var result = await _repository.GetDashboardDataAsync();

        // Assert
        Assert.That(result, Is.Not.Null);
        Assert.That(result!.Id, Is.EqualTo(1));
    }

    [Test]
    public async Task GetDashboardDataByDashboardIdAsync_WithAnswerEvents_PopulatesAnswers()
    {
        // Arrange
        const string DASHBOARD_ID = "TestDashboardId";
        var dashboardData = new SessionDashboardData
        {
            Id = 1,
            DashboardId = DASHBOARD_ID,
            PaymentAmount = 100,
            NumberOfPayments = 1,
            WrongAnswers = 1,
            CorrectAnswers = 2,
            Questions = 3
        };
        _context.SessionDashboardData.Add(dashboardData);

        var timestamp1 = new DateTime(2026, 6, 18, 10, 0, 0);
        var timestamp2 = new DateTime(2026, 6, 18, 10, 5, 0);
        var timestamp3 = new DateTime(2026, 6, 18, 10, 10, 0);

        _context.StoredNotificationEvents.Add(new StoredNotificationEvent
        {
            SessionId = DASHBOARD_ID,
            Type = (int)NotificationEventType.Answer,
            Details = "{\"questionId\":\"q1\",\"selectedOptionId\":\"opt1\",\"isCorrect\":true}",
            TimeStamp = timestamp1
        });

        _context.StoredNotificationEvents.Add(new StoredNotificationEvent
        {
            SessionId = DASHBOARD_ID,
            Type = (int)NotificationEventType.Answer,
            Details = "{\"questionId\":\"q2\",\"selectedOptionId\":\"opt3\",\"isCorrect\":false}",
            TimeStamp = timestamp2
        });

        _context.StoredNotificationEvents.Add(new StoredNotificationEvent
        {
            SessionId = DASHBOARD_ID,
            Type = (int)NotificationEventType.Answer,
            Details = "{\"questionId\":\"q3\",\"selectedOptionId\":\"opt2\",\"isCorrect\":true}",
            TimeStamp = timestamp3
        });

        // Add a purchase event that should be filtered out
        _context.StoredNotificationEvents.Add(new StoredNotificationEvent
        {
            SessionId = DASHBOARD_ID,
            Type = (int)NotificationEventType.PurchaseConfirmation,
            Details = "{\"purchaseId\":\"p1\",\"sessionId\":\"" + DASHBOARD_ID + "\",\"amount\":100,\"status\":\"confirmed\"}",
            TimeStamp = timestamp1
        });

        await _context.SaveChangesAsync();

        // Act
        var result = await _repository.GetDashboardDataByDashboardIdAsync(DASHBOARD_ID);

        // Assert
        result.ShouldNotBeNull();
        result.Answers.ShouldNotBeNull();
        result.Answers.Count.ShouldBe(3);

        result.Answers[0].Item1.ShouldBe(timestamp1);
        result.Answers[0].Item2.QuestionId.ShouldBe("q1");
        result.Answers[0].Item2.SelectedOptionId.ShouldBe("opt1");
        result.Answers[0].Item2.IsCorrect.ShouldBe(true);

        result.Answers[1].Item1.ShouldBe(timestamp2);
        result.Answers[1].Item2.QuestionId.ShouldBe("q2");
        result.Answers[1].Item2.SelectedOptionId.ShouldBe("opt3");
        result.Answers[1].Item2.IsCorrect.ShouldBe(false);

        result.Answers[2].Item1.ShouldBe(timestamp3);
        result.Answers[2].Item2.QuestionId.ShouldBe("q3");
        result.Answers[2].Item2.SelectedOptionId.ShouldBe("opt2");
        result.Answers[2].Item2.IsCorrect.ShouldBe(true);
    }
}
