import { Component, inject, signal } from '@angular/core';
import { ActivatedRoute, Router } from '@angular/router';
import { catchError, of } from 'rxjs';
import { PaymentService } from '../services/paymentservice';

export enum PaymentOutcome {
  Pending = 'pending',
  Confirmed = 'confirmed',
  Cancelled = 'cancelled'
}

@Component({
  selector: 'payment-detail',
  standalone: true,
  templateUrl: './payment-detail.component.html',
  styleUrl: './payment-detail.component.css'
})
export class PaymentDetailComponent {
  private route = inject(ActivatedRoute);
  private router = inject(Router);
  private paymentService = inject(PaymentService);

  // Exposed so the template can reference the enum members.
  public readonly PaymentOutcome = PaymentOutcome;
  public paymentId = this.route.snapshot.paramMap.get('paymentId') ?? '';
  public outcome = signal<PaymentOutcome>(PaymentOutcome.Pending);

  public onConfirm(): void {
    this.paymentService.confirmPayment(this.paymentId)
      .pipe(catchError(err => {
        console.error(err?.message ?? err);
        this.router.navigate(['/error']);
        return of(undefined);
      }))
      .subscribe(confirmation => {
        if (confirmation) {
          this.outcome.set(PaymentOutcome.Confirmed);
        }
      });
  }

  public onCancel(): void {
    this.paymentService.cancelPayment(this.paymentId)
      .pipe(catchError(err => {
        console.error(err?.message ?? err);
        this.router.navigate(['/error']);
        return of(undefined);
      }))
      .subscribe(cancellation => {
        if (cancellation) {
          this.outcome.set(PaymentOutcome.Cancelled);
        }
      });
  }
}
