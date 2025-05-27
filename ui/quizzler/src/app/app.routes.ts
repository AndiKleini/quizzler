import { Routes } from '@angular/router';
import { SinglepickComponent } from './singlepick/singlepick.component';

export const routes: Routes = [ 
    { path: 'singlepick', component: SinglepickComponent },
    { path: '', component: SinglepickComponent}
];
