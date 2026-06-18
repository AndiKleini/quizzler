export interface AnswerDto {
  questionId: string;
  selectedOptionId: string;
  isCorrect: boolean;
}

export interface TimestampedAnswer {
  item1: string; // DateTime as ISO string
  item2: AnswerDto;
}
