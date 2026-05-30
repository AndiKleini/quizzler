export class PaymentConfirmation {
    constructor(
        public confirmationId: string,
        public paymentId: string,
        public createdAt: string
    ) { };
}
