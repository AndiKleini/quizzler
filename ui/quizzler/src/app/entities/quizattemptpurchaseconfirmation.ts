export class QuizAttemptPurchaseConfirmation {
    constructor(
        public confirmationId: string,
        public purchaseId: string,
        public createdAt: string
    ) { };
}
