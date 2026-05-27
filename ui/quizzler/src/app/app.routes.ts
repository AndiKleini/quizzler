import { Routes } from '@angular/router';
import { SinglepickComponent } from './singlepick/singlepick.component';
import { QuizSessionComponent } from './quiz-session/quiz-session.component';
import { QuizAttemptStepComponent } from './quiz-attempt-step/quiz-attempt-step.component';
import { ErrorComponent } from './error/error.component';

export const routes: Routes = [
    { path: 'quiz-session/:sessionId', component: QuizSessionComponent },
    { path: 'quiz-session/:sessionId/attempt-step', component: QuizAttemptStepComponent },
    { path: 'singlepick', component: SinglepickComponent },
    { path: 'error', component: ErrorComponent },
    { path: '', component: SinglepickComponent }
];
