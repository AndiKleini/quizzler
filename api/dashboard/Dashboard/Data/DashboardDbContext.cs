using Dashboard.Models;
using Microsoft.EntityFrameworkCore;

namespace Dashboard.Data;

public class DashboardDbContext : DbContext
{
    public DashboardDbContext(DbContextOptions<DashboardDbContext> options) : base(options)
    {
    }

    public DbSet<SessionDashboardData> SessionDashboardData { get; set; }

    public DbSet<NotificationEvent> NotificationEvents { get; set; }

    protected override void OnModelCreating(ModelBuilder modelBuilder)
    {
        base.OnModelCreating(modelBuilder);

        modelBuilder.Entity<SessionDashboardData>(entity =>
        {
            entity.HasKey(e => e.Id);
            entity.ToTable("SessionDashboardData");
        });

        modelBuilder.Entity<NotificationEvent>(entity =>
        {
            entity.HasKey(e => e.Id);
            entity.ToTable("NotificationEvents");
        });
    }
}
