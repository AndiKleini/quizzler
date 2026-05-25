import { Routes } from '@angular/router';
import { SinglepickComponent } from './singlepick/singlepick.component';
import { QuizSessionComponent } from './quiz-session/quiz-session.component';

export const routes: Routes = [ 
    { path: 'quiz-session/:id', component: QuizSessionComponent },
    { path: 'singlepick', component: SinglepickComponent },
    { path: '', component: SinglepickComponent }
];
