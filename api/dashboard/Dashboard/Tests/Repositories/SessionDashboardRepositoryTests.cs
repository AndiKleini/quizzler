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
        result.ShouldBeEquivalentTo(dashboardData);
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
        result.ShouldBeEquivalentTo(dashboardData);
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
}
