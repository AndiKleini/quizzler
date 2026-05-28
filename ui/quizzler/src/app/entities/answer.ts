export class Answer {
    constructor(
        public id: number,
        public attemptId: string,
        public questionId: number,
        public selectedOptionId: number,
        public submittedAt: string
    ) { };
}
