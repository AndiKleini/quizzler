import { Routes } from '@angular/router';
import { PaymentDetailComponent } from './payment-detail/payment-detail.component';
import { ErrorComponent } from './error/error.component';

export const routes: Routes = [
    { path: 'payment/:paymentId', component: PaymentDetailComponent },
    { path: 'error', component: ErrorComponent },
    { path: '', redirectTo: 'error', pathMatch: 'full' }
];
