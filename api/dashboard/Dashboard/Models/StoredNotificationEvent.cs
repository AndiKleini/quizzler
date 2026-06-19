namespace Dashboard.Models;

public class StoredNotificationEvent
{
    public int Id { get; set; }
    public string SessionId { get; set; } = string.Empty;
    public int Type { get; set; }
    public string Details { get; set; } = string.Empty;
    public DateTime TimeStamp { get; set; }
}
