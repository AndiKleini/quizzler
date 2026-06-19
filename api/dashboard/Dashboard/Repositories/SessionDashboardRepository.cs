using System.Runtime.CompilerServices;
using System.Text.Json;
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
        var dashboardData = await _context.SessionDashboardData.FirstOrDefaultAsync();

        if (dashboardData != null)
        {
            await PopulateAnswersAsync(dashboardData);
        }

        return dashboardData;
    }

    public async Task UpdateDashboardDataAsync(SessionDashboardData dashboardData)
    {
        _context.SessionDashboardData.Update(dashboardData);
        await _context.SaveChangesAsync();
    }

    public async Task<SessionDashboardData> GetDashboardDataByDashboardIdAsync(string dashboardId)
    {
        var dashboardData = await _context.SessionDashboardData.
            SingleOrDefaultAsync(s => s.DashboardId == dashboardId);

        if (dashboardData != null)
        {
            await PopulateAnswersAsync(dashboardData);
        }

        return dashboardData;
    }

    private async Task PopulateAnswersAsync(SessionDashboardData dashboardData)
    {
        var answerEvents = await _context.StoredNotificationEvents
            .Where(e => e.SessionId == dashboardData.DashboardId &&
                        e.Type == (int)NotificationEventType.Answer)
            .OrderBy(e => e.TimeStamp)
            .ToListAsync();

        dashboardData.Answers = answerEvents
            .Select(e =>
            {
                var answerDto = JsonSerializer.Deserialize<AnswerDto>(e.Details, new JsonSerializerOptions
                {
                    PropertyNameCaseInsensitive = true
                });
                return answerDto != null ? new Tuple<DateTime, AnswerDto>(e.TimeStamp, answerDto) : null;
            })
            .Where(tuple => tuple != null)
            .Cast<Tuple<DateTime, AnswerDto>>()
            .ToList();
    }
}
