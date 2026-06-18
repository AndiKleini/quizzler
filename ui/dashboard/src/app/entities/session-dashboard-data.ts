import { TimestampedAnswer } from './answer-dto';

export interface SessionDashboardData {
  id: number;
  dashboardId: string;
  paymentAmount: number;
  numberOfPayments: number;
  wrongAnswers: number;
  correctAnswers: number;
  questions: number;
  answers?: TimestampedAnswer[];
}
