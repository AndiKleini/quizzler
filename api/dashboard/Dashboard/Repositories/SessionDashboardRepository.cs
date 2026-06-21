using System.Runtime.CompilerServices;
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

    public async Task<SessionDashboardData> GetDashboardDataByDashboardIdAsync(string dashboardId)
    {
        return await _context.SessionDashboardData.
            SingleOrDefaultAsync(s => s.DashboardId == dashboardId);
    }

    public bool Exists(string dashboardId)
    {
        return _context.SessionDashboardData.SingleOrDefault(s => s.DashboardId == dashboardId) != null;
    }
}
