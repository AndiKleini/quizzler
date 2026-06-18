using Dashboard.Models;
using Dashboard.Repositories;
using Microsoft.AspNetCore.Mvc;

namespace Dashboard.Controllers;

[ApiController]
[Route("api/[controller]")]
public class SessionDashboardController : ControllerBase
{
    private readonly ISessionDashboardRepository _repository;
    private readonly IStoredNotificationEventRepository _eventRepository;
    private readonly ILogger<SessionDashboardController> _logger;

    public SessionDashboardController(
        ISessionDashboardRepository repository,
        IStoredNotificationEventRepository eventRepository,
        ILogger<SessionDashboardController> logger)
    {
        _repository = repository;
        _eventRepository = eventRepository;
        _logger = logger;
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
            return NotFound();
        }

        // Populate answers from stored events
        var answerEvents = await _eventRepository.GetAnswerEventsBySessionIdAsync(dashboardId);
        data.Answers = answerEvents
            .Select(e => new Tuple<DateTime, AnswerDto>(e.TimeStamp, e.GetDetailsAs<AnswerDto>()!))
            .Where(t => t.Item2 != null)
            .ToList();

        return Ok(data);
    }
}
