using Dashboard.Models;

public interface ISessionDashboardService
{
  Task<SessionDashboardData> GetDashboardFromNotificationEvents(string dashboardId);
}
