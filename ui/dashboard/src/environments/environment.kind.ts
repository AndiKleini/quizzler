// KIND (host-based nginx ingress) environment.
// The dashboard API is reached through its own ingress host on port 80.
export const environment = {
  // dashboard-api, behind api.dashboard.localhost
  apiBaseUrl: 'http://api.dashboard.localhost',
};
