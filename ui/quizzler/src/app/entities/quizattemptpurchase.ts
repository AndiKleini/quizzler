export class QuizAttemptPurchase {
    constructor(
        public purchaseId: string,
        public sessionId: string,
        public price: number
    ) { };
}
