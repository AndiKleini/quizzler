# As a trainer I want to pay quizzers for attempts

, so that I earn money when my created sessions are used.

# description

The quizzers has to pay before an attempt can be started and the quiz consumed.

# solution
- The session provides a buy now button
- The quizzer clicks by now and is redirected to the purchase attempt control.
- The server is called with post quiz-attempt-purchase and returns a redirect url where the customer is redirected to the payment provider.
- The customer confirms / cancels the payment
- The customer is redirected back to the quiz-attempt-purchase control which displays the state of the purchase.
- The customer has to wait seeing a spinner (the control fetches the payment state on a regular basis) until the payment is through.
- The customer is redirected to the attempt