using Dashboard.Data;
using Dashboard.Models;
using Microsoft.EntityFrameworkCore;

namespace Dashboard.Repositories;

public class SessionDashboardRepository : ISessionDashboardRepository
{
    private readonly DashboardDbContext _context;

    public SessionDashboardRepository(DashboardDbContext context)
    {
        _context = context;
    }

    public async Task<SessionDashboardData?> GetDashboardDataAsync()
    {
        return await _context.SessionDashboardData.FirstOrDefaultAsync();
    }

    public async Task UpdateDashboardDataAsync(SessionDashboardData dashboardData)
    {
        _context.SessionDashboardData.Update(dashboardData);
        await _context.SaveChangesAsync();
    }
}
