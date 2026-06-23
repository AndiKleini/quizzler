import { Component, OnInit, inject, signal } from '@angular/core';
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
export class PaymentDetailComponent implements OnInit {

  private route = inject(ActivatedRoute);
  private router = inject(Router);
  private paymentService = inject(PaymentService);

  // Exposed so the template can reference the enum members.
  public readonly PaymentOutcome = PaymentOutcome;
  public paymentId = this.route.snapshot.paramMap.get('paymentId') ?? '';
  public outcome = signal<PaymentOutcome>(PaymentOutcome.Pending);
  public price = signal<number | undefined>(undefined);
  public isLoading = signal<boolean>(false);

  // URL to return the user to once the payment is settled (supplied by the merchant).
  private redirectUrl = '';

  public ngOnInit(): void {
    this.paymentService.getPayment(this.paymentId)
      .pipe(
        catchError(err => {
          console.error(err?.message ?? err);
          this.router.navigate(['/error']);
          return of(undefined);
        }))
      .subscribe(payment => {
        if (payment) {
          this.price.set(payment.price);
          this.redirectUrl = payment.redirectUrl;
        }
      });
  }

  public onConfirm(): void {
    this.isLoading.set(true);
    this.paymentService.confirmPayment(this.paymentId)
      .pipe(catchError(err => {
        console.error(err?.message ?? err);
        this.router.navigate(['/error']);
        this.isLoading.set(false);
        return of(undefined);
      }))
      .subscribe(confirmation => {
        if (confirmation) {
          this.outcome.set(PaymentOutcome.Confirmed);
          this.redirect(this.redirectUrl);
        }
        this.isLoading.set(false);
      });
  }

  // Full-page navigation to the merchant's redirect URL (a different origin than this app).
  protected redirect(url: string): void {
    window.location.href = url;
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
