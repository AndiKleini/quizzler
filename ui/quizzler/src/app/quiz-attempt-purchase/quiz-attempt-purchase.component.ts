import { Component, inject } from '@angular/core';
import { ActivatedRoute, Router } from '@angular/router';
import { catchError, of } from 'rxjs';
import { QuizAttemptService } from '../services/quiz-attemptservice';

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
