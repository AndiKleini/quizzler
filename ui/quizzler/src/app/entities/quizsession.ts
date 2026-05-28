export class QuizSession {
    constructor(
        public publicId: string
    ) { };

    public static getDefaultQuizSession() : QuizSession {
            return new QuizSession('0');
    }

    public isDefault() : boolean {
        return this.publicId === QuizSession.getDefaultQuizSession().publicId;
    }
}
