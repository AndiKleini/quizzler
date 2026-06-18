using Dashboard.Models;
using Microsoft.EntityFrameworkCore;

namespace Dashboard.Data;

public class DashboardDbContext : DbContext
{
    public DashboardDbContext(DbContextOptions<DashboardDbContext> options) : base(options)
    {
    }

    public DbSet<SessionDashboardData> SessionDashboardData { get; set; }
    public DbSet<StoredNotificationEvent> StoredNotificationEvents { get; set; }

    protected override void OnModelCreating(ModelBuilder modelBuilder)
    {
        base.OnModelCreating(modelBuilder);

        modelBuilder.Entity<SessionDashboardData>(entity =>
        {
            entity.HasKey(e => e.Id);
            entity.ToTable("SessionDashboardData");
        });

        modelBuilder.Entity<StoredNotificationEvent>(entity =>
        {
            entity.HasKey(e => e.Id);
            entity.ToTable("StoredNotificationEvents");
            entity.HasIndex(e => e.SessionId);
            entity.Property(e => e.Details).IsRequired();
            entity.Property(e => e.SessionId).IsRequired();
        });
    }
}
