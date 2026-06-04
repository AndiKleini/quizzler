// KIND (host-based nginx ingress) environment.
// payment-api is reached through its own ingress host on port 80.
export const environment = {
  // payment-api, behind api.payment.localhost
  apiBaseUrl: 'http://api.payment.localhost',
};
