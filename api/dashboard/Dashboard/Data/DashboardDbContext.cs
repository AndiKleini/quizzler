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
    public DbSet<StoredAnswerDetails> StoredAnswerDetails { get; set; }
    public DbSet<StoredPurchaseConfirmationDetails> StoredPurchaseConfirmationDetails { get; set; }

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
            entity.Property(e => e.SessionId).IsRequired();

            entity.HasOne(e => e.AnswerDetails)
                .WithOne(d => d.StoredNotificationEvent)
                .HasForeignKey<StoredAnswerDetails>(d => d.StoredNotificationEventId)
                .OnDelete(DeleteBehavior.Cascade);

            entity.HasOne(e => e.PurchaseConfirmationDetails)
                .WithOne(d => d.StoredNotificationEvent)
                .HasForeignKey<StoredPurchaseConfirmationDetails>(d => d.StoredNotificationEventId)
                .OnDelete(DeleteBehavior.Cascade);
        });

        modelBuilder.Entity<StoredAnswerDetails>(entity =>
        {
            entity.HasKey(e => e.Id);
            entity.ToTable("StoredAnswerDetails");
            entity.Property(e => e.QuestionId).IsRequired();
            entity.Property(e => e.SelectedOptionId).IsRequired();
        });

        modelBuilder.Entity<StoredPurchaseConfirmationDetails>(entity =>
        {
            entity.HasKey(e => e.Id);
            entity.ToTable("StoredPurchaseConfirmationDetails");
            entity.Property(e => e.PurchaseId).IsRequired();
            entity.Property(e => e.SessionId).IsRequired();
            entity.Property(e => e.Status).IsRequired();
        });
    }
}
