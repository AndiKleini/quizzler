# Notification Events - ER Diagram

## Entity Relationship Diagram

```mermaid
erDiagram
    SessionDashboardData ||--o{ StoredNotificationEvent : "tracked by"
    
    SessionDashboardData {
        int Id PK
        string DashboardId UK "Session identifier"
        int PaymentAmount "Aggregated payment total"
        int NumberOfPayments "Count of payments"
        int WrongAnswers "Count of wrong answers"
        int CorrectAnswers "Count of correct answers"
        int Questions "Total questions answered"
    }
    
    StoredNotificationEvent {
        int Id PK
        string SessionId FK "References DashboardId"
        int Type "NotificationEventType enum"
        json Details "AnswerDto or QuizAttemptPurchaseConfirmationDto"
        datetime TimeStamp "Event timestamp"
    }
    
    StoredNotificationEvent ||--|| NotificationEventType : "typed by"
    StoredNotificationEvent ||--o| AnswerDto : "contains (Type=2)"
    StoredNotificationEvent ||--o| QuizAttemptPurchaseConfirmationDto : "contains (Type=1)"
    
    NotificationEventType {
        int PurchaseConfirmation "1"
        int Answer "2"
    }
    
    AnswerDto {
        string QuestionId
        string SelectedOptionId
        bool IsCorrect
    }
    
    QuizAttemptPurchaseConfirmationDto {
        string PurchaseId
        string SessionId
        int Amount
        string Status
    }
```

## Data Flow

```mermaid
flowchart TD
    A[NotificationEvent arrives] --> B{Factory: Check Session Exists?}
    B -->|Yes| C[NotificationEventHandlerService]
    B -->|No| D[StreamNotificationEventHandlerService]
    
    C --> E[Deserialize Details JSON]
    E --> F{Event Type?}
    F -->|Type=1| G[QuizAttemptPurchaseConfirmationDto]
    F -->|Type=2| H[AnswerDto]
    G --> I[ApplyTo SessionDashboardData]
    H --> I
    I --> J[Update aggregated values]
    
    D --> K[Create StoredNotificationEvent]
    K --> L[Store as event stream]
    L --> M[(StoredNotificationEvents Table)]
    
    style C fill:#90EE90
    style D fill:#87CEEB
    style M fill:#FFE4B5
```

## Details JSON Structure

### For Type = 1 (PurchaseConfirmation)
```json
{
  "purchaseId": "string",
  "sessionId": "string",
  "amount": 0,
  "status": "string"
}
```

### For Type = 2 (Answer)
```json
{
  "questionId": "string",
  "selectedOptionId": "string",
  "isCorrect": true
}
```

## Storage Strategy

| Session Status | Handler Service | Action |
|---------------|----------------|--------|
| **New Session** (not exists) | `StreamNotificationEventHandlerService` | Store events as stream in `StoredNotificationEvents` table with JSON details |
| **Existing Session** | `NotificationEventHandlerService` | Deserialize JSON, apply updates to aggregated `SessionDashboardData` |

## Database Schema

### StoredNotificationEvents Table
```sql
CREATE TABLE StoredNotificationEvents (
    Id INT PRIMARY KEY IDENTITY(1,1),
    SessionId NVARCHAR(MAX) NOT NULL,
    Type INT NOT NULL,
    Details JSON NOT NULL,  -- Native JSON column type
    TimeStamp DATETIME2 NOT NULL
);

-- Recommended index for session queries
CREATE INDEX IX_StoredNotificationEvents_SessionId_TimeStamp 
ON StoredNotificationEvents(SessionId, TimeStamp);
```

### SessionDashboardData Table
```sql
CREATE TABLE SessionDashboardData (
    Id INT PRIMARY KEY IDENTITY(1,1),
    DashboardId NVARCHAR(MAX) NOT NULL,
    PaymentAmount INT NOT NULL DEFAULT 0,
    NumberOfPayments INT NOT NULL DEFAULT 0,
    WrongAnswers INT NOT NULL DEFAULT 0,
    CorrectAnswers INT NOT NULL DEFAULT 0,
    Questions INT NOT NULL DEFAULT 0
);
```

## Notes

- The `Details` column uses SQL Server's native **JSON** data type for efficient storage and querying
- `StoredNotificationEvent.SessionId` logically references `SessionDashboardData.DashboardId` but is not a formal FK constraint
- Events are stored chronologically ordered by `TimeStamp`
- The factory pattern allows different handling strategies based on session lifecycle
