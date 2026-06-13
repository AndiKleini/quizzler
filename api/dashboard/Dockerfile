# syntax=docker/dockerfile:1

# --- Build stage -----------------------------------------------------------
FROM mcr.microsoft.com/dotnet/sdk:10.0 AS build
WORKDIR /src

# Copy csproj and restore dependencies
COPY Dashboard/Dashboard.csproj Dashboard/
RUN dotnet restore Dashboard/Dashboard.csproj

# Copy the rest of the source code
COPY Dashboard/ Dashboard/
WORKDIR /src/Dashboard
RUN dotnet build Dashboard.csproj -c Release -o /app/build

# --- Publish stage ---------------------------------------------------------
FROM build AS publish
RUN dotnet publish Dashboard.csproj -c Release -o /app/publish /p:UseAppHost=false

# --- Runtime stage ---------------------------------------------------------
FROM mcr.microsoft.com/dotnet/aspnet:10.0 AS runtime
WORKDIR /app

# Run as an unprivileged user
RUN useradd --system --uid 1001 dotnet
USER dotnet

COPY --from=publish /app/publish .

EXPOSE 8080
EXPOSE 8081

ENV ASPNETCORE_URLS=http://+:8080

ENTRYPOINT ["dotnet", "Dashboard.dll"]
