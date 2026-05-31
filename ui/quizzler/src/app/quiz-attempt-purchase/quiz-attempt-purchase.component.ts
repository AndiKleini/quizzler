import { Component, inject } from '@angular/core';
import { ActivatedRoute, Router } from '@angular/router';
import { catchError, of } from 'rxjs';
import { QuizAttemptService } from '../services/quiz-attemptservice';

const paymentUiBaseUrl = 'http://localhost:4201';
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
  private quizAttemptService = inject(QuizAttemptService);

  public sessionId = this.route.snapshot.paramMap.get('sessionId') ?? '';
  public purchaseId = this.route.snapshot.paramMap.get('purchaseId') ?? '';
  public priceInCents = (this.router.getCurrentNavigation()?.extras.state as { price?: number } | undefined)?.price ?? 0;

  public get priceLabel(): string {
    return `${(this.priceInCents / centsPerEuro).toFixed(2)} €`;
  }

  public onStartPayment(): void {

    // TODO start payment here and navigate to payment detail page after successful payment initiation. For now, we just navigate to the payment UI with the purchase ID as a path parameter, where the user can confirm or cancel the payment. The payment detail page will then call the appropriate API to confirm or cancel the payment.

    window.location.href = `${paymentUiBaseUrl}/payment/${this.purchaseId}`;
  }

  public onStart(): void {
    this.quizAttemptService.createAttempt(this.sessionId, this.purchaseId)
      .pipe(catchError(err => {
        console.error(err?.message ?? err);
        this.router.navigate(['/error']);
        return of(undefined);
      }))
      .subscribe(attempt => {
        if (attempt) {
          this.router.navigate(
            ['/quiz-session', attempt.sessionId, 'attempt', attempt.attemptId, 'question', attempt.questionId]);
        }
      });
  }
}
