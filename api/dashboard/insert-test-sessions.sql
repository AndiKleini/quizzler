-- Insert test sessions used by NotificationEventPublisher
-- These sessions match the sessionIds array in NotificationEventPublisher/Program.cs

USE dashboard;
GO

-- Clear existing test data (optional)
DELETE FROM SessionDashboardData
WHERE DashboardId IN ('session-001', 'session-002', 'session-003');
GO

-- Insert session-001
INSERT INTO SessionDashboardData (DashboardId, PaymentAmount, NumberOfPayments, WrongAnswers, CorrectAnswers, Questions)
VALUES ('session-001', 0, 0, 0, 0, 0);
GO

-- Insert session-002
INSERT INTO SessionDashboardData (DashboardId, PaymentAmount, NumberOfPayments, WrongAnswers, CorrectAnswers, Questions)
VALUES ('session-002', 0, 0, 0, 0, 0);
GO

-- Insert session-003
INSERT INTO SessionDashboardData (DashboardId, PaymentAmount, NumberOfPayments, WrongAnswers, CorrectAnswers, Questions)
VALUES ('session-003', 0, 0, 0, 0, 0);
GO

-- Verify the inserts
SELECT * FROM SessionDashboardData WHERE DashboardId IN ('session-001', 'session-002', 'session-003');
GO
