export class QuizSession {
    constructor(
        public publicId: string,
        public currentQuestion: number,
        public nextQuestion: number,
        public previousQuestion: number
    ) { };

    public static getDefaultQuizSession() : QuizSession {
            return new QuizSession('0', 0, 1, -1);
    }
}
