using System;
using Microsoft.EntityFrameworkCore.Migrations;

#nullable disable

namespace Dashboard.Migrations
{
    /// <inheritdoc />
    public partial class AddStoredNotificationEvents : Migration
    {
        /// <inheritdoc />
        protected override void Up(MigrationBuilder migrationBuilder)
        {
            migrationBuilder.CreateTable(
                name: "StoredNotificationEvents",
                columns: table => new
                {
                    Id = table.Column<int>(type: "int", nullable: false)
                        .Annotation("SqlServer:Identity", "1, 1"),
                    SessionId = table.Column<string>(type: "nvarchar(450)", nullable: false),
                    Type = table.Column<int>(type: "int", nullable: false),
                    Details = table.Column<string>(type: "nvarchar(max)", nullable: false),
                    TimeStamp = table.Column<DateTime>(type: "datetime2", nullable: false)
                },
                constraints: table =>
                {
                    table.PrimaryKey("PK_StoredNotificationEvents", x => x.Id);
                });

            migrationBuilder.CreateIndex(
                name: "IX_StoredNotificationEvents_SessionId",
                table: "StoredNotificationEvents",
                column: "SessionId");
        }

        /// <inheritdoc />
        protected override void Down(MigrationBuilder migrationBuilder)
        {
            migrationBuilder.DropTable(
                name: "StoredNotificationEvents");
        }
    }
}
