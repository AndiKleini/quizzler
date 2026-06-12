using Dashboard.Models;

namespace Dashboard.Repositories;

public interface ISessionDashboardRepository
{
    Task<SessionDashboardData?> GetDashboardDataAsync();
}
