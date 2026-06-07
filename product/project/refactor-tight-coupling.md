# Lists the refactoring steps for the tight coupling between payment and quizzler

## 1. Introduce event publishing and sourcing as additional and redundant integration

  A strangler-style dual-write: the payment API keeps its existing HTTP
  success-webhook call and additionally publishes a PaymentConfirmed message;
  the quizzler API gains a listener that confirms the purchase from that
  message. The HTTP path stays authoritative for now, so nothing breaks if the
  broker is down — a later step can remove the HTTP coupling.

  **Message contract / topology**

  - Topic exchange payment.events, routing key payment.confirmed, queue
  quizzler.payment-confirmed (owned by the quizzler side).
  - Payload PaymentConfirmedEvent { paymentId, transactionId, confirmedAt }.
  transactionId is the quizzler purchase reference the payment API already
  stores — that's the correlation key. JSON via a Jackson2JsonMessageConverter
  built from the Spring ObjectMapper (so Instant (de)serializes).

  **Payment API (publisher)**

  - config/RabbitConfig — exchange + converter beans + shared constants.
  - messaging/PaymentConfirmedEvent, messaging/PaymentConfirmationPublisher —
  publishing is best-effort: an AmqpException is logged and swallowed so a
  broker outage never fails confirmation.
  - PaymentConfirmationService.confirmPayment — publishes the event right after
  notifyConfirmation.
  - pom.xml + application.properties (rabbitmq connection), service test
  extended to verify the event.

  **Quizzler API (consumer)**

  - config/RabbitConfig — exchange + queue + binding + converter.
  - messaging/PaymentConfirmedEvent, messaging/PaymentConfirmationListener
  (@RabbitListener) → calls confirmPurchase.
  - QuizAttemptPurchaseService.confirmPurchase(purchaseId) — new idempotent 
  overload: derives the session from the purchase and returns the existing
  confirmation instead of throwing 409 (since the HTTP webhook usually already
  confirmed it). The existing two-arg method (HTTP contract, still throws on
  duplicate) is untouched. I also removed the stray debug System.out.println
  lines from the existing method while I was in there.
  - pom.xml, application.properties (default-requeue-rejected=false so a poison
  message is dropped, not looped), test props
  (listener.simple.auto-startup=false so the suites don't need a broker), plus
  service + listener tests.

Babysteps:
- add RabbitMqConfiguration in KIND Cluster to quizzler and payment-api
- (can be omitted for presentation) adapt configuration settings for docker compose file
- Build in messaging for payment API:
    - PaymentPublisher
    - RabbitConfig, 
    - PaymentConfirmedEvent 
    - Add publishing call in PaymentConfirmationService


## 2. Remove the http call from payment to quizzler api