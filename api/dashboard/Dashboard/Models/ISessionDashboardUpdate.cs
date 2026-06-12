namespace Dashboard.Models;

public interface ISessionDashboardUpdate
{
    void ApplyTo(SessionDashboardData dashboard);
}
