import { Routes } from '@angular/router';
import { DashboardComponent } from './dashboard/dashboard.component';

export const routes: Routes = [
  { path: 'dashboard/:dashboardId', component: DashboardComponent },
  { path: '', redirectTo: '/dashboard/11111111-2222-3333-4444-555555555555', pathMatch: 'full' }
];
