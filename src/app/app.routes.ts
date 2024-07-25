import { Routes, RouterOutlet} from '@angular/router';
import { SinglePickComponent } from './single-pick/single-pick.component';
import { AppComponent } from './app.component';

export const routes: Routes = [
    { path: '', component: AppComponent },
    { path: 'singlepick/:id', component: SinglePickComponent}
];