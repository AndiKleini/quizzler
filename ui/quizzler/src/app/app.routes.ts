import { Routes } from '@angular/router';
import { SinglepickComponent } from './singlepick/singlepick.component';
import { QuizSessionComponent } from './quiz-session/quiz-session.component';
import { ErrorComponent } from './error/error.component';

export const routes: Routes = [
    { path: 'quiz-session/:id', component: QuizSessionComponent },
    { path: 'singlepick', component: SinglepickComponent },
    { path: 'error', component: ErrorComponent },
    { path: '', component: SinglepickComponent }
];
