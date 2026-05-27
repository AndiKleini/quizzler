export class QuizAttempt {
    constructor(
        public attemptId: string,
        public sessionId: string,
        public questionId: number
    ) { };
}
