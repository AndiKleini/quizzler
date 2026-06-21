namespace Dashboard.Models;

public class NotificationEvent
{
    public int Id { get; internal set; }
    public string SessionId { get; set; } = string.Empty;
    public int Type { get; set; }
    public string Details { get; set; } = string.Empty;
    public DateTime TimeStamp { get; set; }
}
