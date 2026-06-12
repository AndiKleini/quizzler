#!/bin/bash

# Start SQL Server 2025 container
docker run -d \
  --name sqlserver-dashboard \
  -e "ACCEPT_EULA=Y" \
  -e "SA_PASSWORD=Dboard123\!" \
  -e "MSSQL_PID=Developer" \
  -p 1433:1433 \
  mcr.microsoft.com/mssql/server:2025-latest

echo "Waiting for SQL Server to start..."
sleep 20

# Create the database
docker exec -it sqlserver-dashboard /opt/mssql-tools18/bin/sqlcmd \
  -S localhost -U sa -P Dboard123\! -C \
  -Q "CREATE DATABASE dashboardDb;"

echo "Database created. Waiting for it to be ready..."
sleep 5

# Create table and insert dummy data
docker exec -it sqlserver-dashboard /opt/mssql-tools18/bin/sqlcmd \
  -S localhost -U sa -P Dboard123\! -C -d dashboardDb \
  -Q "CREATE TABLE SessionDashboardData (
    Id INT PRIMARY KEY IDENTITY(1,1),
    PaymentAmount INT NOT NULL,
    NumberOfPayments INT NOT NULL,
    WrongAnswers INT NOT NULL,
    CorrectAnswers INT NOT NULL,
    Questions INT NOT NULL
  );

  INSERT INTO SessionDashboardData (PaymentAmount, NumberOfPayments, WrongAnswers, CorrectAnswers, Questions)
  VALUES
    (250, 5, 2, 8, 10),
    (500, 10, 5, 15, 20),
    (150, 3, 1, 4, 5);"

echo "SQL Server is ready with dummy data!"
echo "Connection String: Server=localhost,1433;Database=dashboardDb;User Id=sa;Password=Dboard123!;TrustServerCertificate=True;"
