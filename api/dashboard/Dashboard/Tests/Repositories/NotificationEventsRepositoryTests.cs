
using Dashboard.Data;
using Dashboard.Models;
using Microsoft.EntityFrameworkCore;
using NUnit.Framework;
using Shouldly;

[TestFixture]
public class NotificationEventsRepositoryTests
{
  private const string DASHBOARD_ID = "SomeSessionId";

  [Test]
    public async Task GetEventsForDashboard_DashboardIdSupplied_ReturnsListOfAssociatedEvents()
    {
        DashboardDbContext context = new DashboardDbContext(
            new DbContextOptionsBuilder<DashboardDbContext>().
            UseInMemoryDatabase(Guid.NewGuid().ToString()).Options);
        NotificationEvent receivedEventNo1 = 
            new NotificationEvent()
            {
                SessionId = DASHBOARD_ID,
                TimeStamp = DateTime.Now, // will not cause flakiness
                Details = @"{
                                ""questionId"": ""selection_1"",
                                ""isCorrect"": false,
                                ""selectedOptionId"": ""option_1""
                            }"
        };
        NotificationEvent receivedEventNo2 = 
            new NotificationEvent()
            {
                SessionId = DASHBOARD_ID,
                TimeStamp = DateTime.Now, // will not cause flakiness
                Details = @"{
                                ""questionId"": ""selection_1"",
                                ""isCorrect"": false,
                                ""selectedOptionId"": ""option_1""
                            }"
            };
        await context.AddAsync(receivedEventNo1);
        await context.AddAsync(receivedEventNo2);
        await context.SaveChangesAsync();
        NotificationEventRepository instanceUnderTest = 
            new NotificationEventRepository(context);
        
        var receivedEvents = await instanceUnderTest.GetNotificationEventsForDashboardId(DASHBOARD_ID);

        receivedEvents.ShouldContain(receivedEventNo1);
        receivedEvents.ShouldContain(receivedEventNo2);
    }
}