using System;
using Microsoft.EntityFrameworkCore.Migrations;

#nullable disable

namespace Dashboard.Migrations
{
    /// <inheritdoc />
    public partial class RefactorNotificationEventStorage : Migration
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
                    TimeStamp = table.Column<DateTime>(type: "datetime2", nullable: false)
                },
                constraints: table =>
                {
                    table.PrimaryKey("PK_StoredNotificationEvents", x => x.Id);
                });

            migrationBuilder.CreateTable(
                name: "StoredAnswerDetails",
                columns: table => new
                {
                    Id = table.Column<int>(type: "int", nullable: false)
                        .Annotation("SqlServer:Identity", "1, 1"),
                    QuestionId = table.Column<string>(type: "nvarchar(max)", nullable: false),
                    SelectedOptionId = table.Column<string>(type: "nvarchar(max)", nullable: false),
                    IsCorrect = table.Column<bool>(type: "bit", nullable: false),
                    StoredNotificationEventId = table.Column<int>(type: "int", nullable: false)
                },
                constraints: table =>
                {
                    table.PrimaryKey("PK_StoredAnswerDetails", x => x.Id);
                    table.ForeignKey(
                        name: "FK_StoredAnswerDetails_StoredNotificationEvents_StoredNotificationEventId",
                        column: x => x.StoredNotificationEventId,
                        principalTable: "StoredNotificationEvents",
                        principalColumn: "Id",
                        onDelete: ReferentialAction.Cascade);
                });

            migrationBuilder.CreateTable(
                name: "StoredPurchaseConfirmationDetails",
                columns: table => new
                {
                    Id = table.Column<int>(type: "int", nullable: false)
                        .Annotation("SqlServer:Identity", "1, 1"),
                    PurchaseId = table.Column<string>(type: "nvarchar(max)", nullable: false),
                    SessionId = table.Column<string>(type: "nvarchar(max)", nullable: false),
                    Amount = table.Column<int>(type: "int", nullable: false),
                    Status = table.Column<string>(type: "nvarchar(max)", nullable: false),
                    StoredNotificationEventId = table.Column<int>(type: "int", nullable: false)
                },
                constraints: table =>
                {
                    table.PrimaryKey("PK_StoredPurchaseConfirmationDetails", x => x.Id);
                    table.ForeignKey(
                        name: "FK_StoredPurchaseConfirmationDetails_StoredNotificationEvents_StoredNotificationEventId",
                        column: x => x.StoredNotificationEventId,
                        principalTable: "StoredNotificationEvents",
                        principalColumn: "Id",
                        onDelete: ReferentialAction.Cascade);
                });

            migrationBuilder.CreateIndex(
                name: "IX_StoredAnswerDetails_StoredNotificationEventId",
                table: "StoredAnswerDetails",
                column: "StoredNotificationEventId",
                unique: true);

            migrationBuilder.CreateIndex(
                name: "IX_StoredNotificationEvents_SessionId",
                table: "StoredNotificationEvents",
                column: "SessionId");

            migrationBuilder.CreateIndex(
                name: "IX_StoredPurchaseConfirmationDetails_StoredNotificationEventId",
                table: "StoredPurchaseConfirmationDetails",
                column: "StoredNotificationEventId",
                unique: true);
        }

        /// <inheritdoc />
        protected override void Down(MigrationBuilder migrationBuilder)
        {
            migrationBuilder.DropTable(
                name: "StoredAnswerDetails");

            migrationBuilder.DropTable(
                name: "StoredPurchaseConfirmationDetails");

            migrationBuilder.DropTable(
                name: "StoredNotificationEvents");
        }
    }
}
