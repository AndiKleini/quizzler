import { Component, inject } from '@angular/core';
import { ActivatedRoute, Router } from '@angular/router';
import { catchError, of } from 'rxjs';
import { QuizAttemptPurchaseService } from '../services/quiz-attempt-purchaseservice';
import { environment } from '../../environments/environment';

const paymentUiBaseUrl = environment.paymentUiBaseUrl;
const centsPerEuro = 100;

@Component({
  selector: 'quizzler-quiz-attempt-purchase',
  standalone: true,
  templateUrl: './quiz-attempt-purchase.component.html',
  styleUrl: './quiz-attempt-purchase.component.css'
})
export class QuizAttemptPurchaseComponent {
  private route = inject(ActivatedRoute);
  private router = inject(Router);
  private quizAttemptPurchaseService = inject(QuizAttemptPurchaseService);

  public sessionId = this.route.snapshot.paramMap.get('sessionId') ?? '';
  public purchaseId = this.route.snapshot.paramMap.get('purchaseId') ?? '';
  public priceInCents = (this.router.getCurrentNavigation()?.extras.state as { price?: number } | undefined)?.price ?? 0;

  public get priceLabel(): string {
    return `${(this.priceInCents / centsPerEuro).toFixed(2)} €`;
  }

  public onStartPayment(): void {
    this.quizAttemptPurchaseService.initiatePayment(this.sessionId, this.purchaseId)
      .pipe(catchError(err => {
        console.error(err?.message ?? err);
        this.router.navigate(['/error']);
        return of(undefined);
      }))
      .subscribe(paymentId => {
        if (paymentId) {
          window.location.href = `${paymentUiBaseUrl}/payment/${paymentId}`;
        }
      });
  }
}
