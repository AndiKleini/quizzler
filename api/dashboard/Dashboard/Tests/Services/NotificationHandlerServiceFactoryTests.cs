using Dashboard.Repositories;
using Dashboard.Services;
using Microsoft.Extensions.Logging;
using Moq;
using NUnit.Framework;
using Shouldly;

[TestFixture]
public class NotificationHandlerServiceFactoryTests
{
    private const string DASHBOARD_ID = "SomeDashboardId";

    [Test]
    public void GetInstance_DashboardExists_ReturnsNotificationEventHandlerService()
    {
        Mock<ISessionDashboardRepository> repositoryMock =
            new Mock<ISessionDashboardRepository>();
        repositoryMock.Setup(m => m.Exists(DASHBOARD_ID)).Returns(true);
        NotificationEventHandlerService notificationEventHandlerService =
            new NotificationEventHandlerService(
                Mock.Of<ILogger<NotificationEventHandlerService>>(),
                repositoryMock.Object);
        StreamNotificationEventHandlerService streamNotificationEventHandlerService =
            new StreamNotificationEventHandlerService(
                Mock.Of<INotificationEventRepository>());
        NotificationHandlerServiceFactory instanceUnderTest =
            new NotificationHandlerServiceFactory(
                repositoryMock.Object,
                notificationEventHandlerService,
                streamNotificationEventHandlerService);

        INotificationEventHandlerService instance = instanceUnderTest.GetInstance(DASHBOARD_ID);

        instance.ShouldBeSameAs(notificationEventHandlerService);
    }

    [Test]
    public void GetInstance_DashboardDoesNotExist_ReturnsStreamNotificationEventHandlerService()
    {
        Mock<ISessionDashboardRepository> repositoryMock =
            new Mock<ISessionDashboardRepository>();
        repositoryMock.Setup(m => m.Exists(DASHBOARD_ID)).Returns(false);
        NotificationEventHandlerService notificationEventHandlerService =
            new NotificationEventHandlerService(
                Mock.Of<ILogger<NotificationEventHandlerService>>(),
                repositoryMock.Object);
        StreamNotificationEventHandlerService streamNotificationEventHandlerService =
            new StreamNotificationEventHandlerService(
                Mock.Of<INotificationEventRepository>());
        NotificationHandlerServiceFactory instanceUnderTest =
            new NotificationHandlerServiceFactory(
                repositoryMock.Object,
                notificationEventHandlerService,
                streamNotificationEventHandlerService);

        INotificationEventHandlerService instance = instanceUnderTest.GetInstance(DASHBOARD_ID);

        instance.ShouldBeSameAs(streamNotificationEventHandlerService);
    }
}
