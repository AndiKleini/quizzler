using Dashboard.Models;

namespace Dashboard.Repositories;

public interface ISessionDashboardRepository
{
    Task<SessionDashboardData?> GetDashboardDataAsync();

    Task UpdateDashboardDataAsync(SessionDashboardData dashboardData);

    Task<SessionDashboardData> GetDashboardDataByDashboardIdAsync(string dashBoardId);
}
