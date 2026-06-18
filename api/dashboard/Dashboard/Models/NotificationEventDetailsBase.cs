namespace Dashboard.Models;

public abstract class NotificationEventDetailsBase
{
    public int Id { get; set; }
    public int StoredNotificationEventId { get; set; }
    public StoredNotificationEvent? StoredNotificationEvent { get; set; }
}
