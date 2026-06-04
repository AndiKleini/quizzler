// Default (development / docker-compose) environment.
// Browser-facing URLs are exposed on localhost by docker-compose.
export const environment = {
  // quizzler-api
  apiBaseUrl: 'http://localhost:8080',
  // payment-ui (full-page redirect target when starting a payment)
  paymentUiBaseUrl: 'http://localhost:4201',
};
