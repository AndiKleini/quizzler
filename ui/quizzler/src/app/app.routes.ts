import { Routes } from '@angular/router';
import { QuizSessionComponent } from './quiz-session/quiz-session.component';
import { QuizAttemptStepComponent } from './quiz-attempt-step/quiz-attempt-step.component';
import { ErrorComponent } from './error/error.component';
import { QuizAttemptStepFinalComponent } from './quiz-attempt-step-final/quiz-attempt-step-final.component';
import { QuizAttemptPurchaseComponent } from './quiz-attempt-purchase/quiz-attempt-purchase.component';
import { QuizAttemptPurchaseConfirmedComponent } from './quiz-attempt-purchase-confirmed/quiz-attempt-purchase-confirmed.component';

export const routes: Routes = [
    { path: 'quiz-session/:sessionId', component: QuizSessionComponent },
    { path: 'quiz-session/:sessionId/quiz-attempt-purchase/:purchaseId', component: QuizAttemptPurchaseComponent },
    { path: 'quiz-session/:sessionId/quiz-attempt-purchase-confirmed', component: QuizAttemptPurchaseConfirmedComponent },
    { path: 'quiz-session/:sessionId/attempt/:attemptId', component: QuizAttemptStepFinalComponent },
    { path: 'quiz-session/:sessionId/attempt/:attemptId/question/:questionId', component: QuizAttemptStepComponent },
    { path: 'error', component: ErrorComponent },
    { path: '', redirectTo: 'error', pathMatch: 'full' }
];
