using Dashboard.Models;
using Dashboard.Repositories;
using Microsoft.AspNetCore.Mvc;

namespace Dashboard.Controllers;

[ApiController]
[Route("api/[controller]")]
public class SessionDashboardController : ControllerBase
{
    private readonly ISessionDashboardRepository _repository;
    private readonly ILogger<SessionDashboardController> _logger;
    private readonly ISessionDashboardService _dashboardService;

    public SessionDashboardController(
        ISessionDashboardRepository repository,
        ILogger<SessionDashboardController> logger,
        ISessionDashboardService dashboardService)
    {
        _repository = repository;
        _logger = logger;
        _dashboardService = dashboardService;
    }

    [HttpGet]
    public async Task<ActionResult<SessionDashboardData>> GetDashboard()
    {
        var data = await _repository.GetDashboardDataAsync();

        if (data == null)
        {
            return NotFound();
        }

        return Ok(data);
    }

    [HttpGet("{dashboardId}")]
    public async Task<ActionResult<SessionDashboardData>> GetDashboardById(string dashboardId)
    {
        var data = await _repository.GetDashboardDataByDashboardIdAsync(dashboardId);

        if (data == null)
        {
            data = await _dashboardService.GetDashboardFromNotificationEvents(dashboardId);

            if (data == null)
            {
                return NotFound();
            }
        }

        return Ok(data);
    }
}
