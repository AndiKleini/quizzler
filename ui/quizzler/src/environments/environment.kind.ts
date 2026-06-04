// KIND (host-based nginx ingress) environment.
// Each capability is reached through its own ingress host on port 80.
export const environment = {
  // quizzler-api, behind api.quizzler.localhost
  apiBaseUrl: 'http://api.quizzler.localhost',
  // payment-ui, behind payment.localhost
  paymentUiBaseUrl: 'http://payment.localhost',
};
