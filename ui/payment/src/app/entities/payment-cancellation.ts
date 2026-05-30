export class PaymentCancellation {
    constructor(
        public cancellationId: string,
        public paymentId: string,
        public createdAt: string
    ) { };
}
