import { Component, OnDestroy, OnInit, inject, signal } from '@angular/core';
import { ActivatedRoute, Router } from '@angular/router';
import { Subscription, catchError, filter, of, switchMap, take, timer } from 'rxjs';
import { QuizAttemptService } from '../services/quiz-attemptservice';
import { QuizAttemptPurchaseService } from '../services/quiz-attempt-purchaseservice';

const pollIntervalMs = 5000;

@Component({
  selector: 'quizzler-quiz-attempt-purchase-confirmed',
  standalone: true,
  templateUrl: './quiz-attempt-purchase-confirmed.component.html',
  styleUrl: './quiz-attempt-purchase-confirmed.component.css'
})
export class QuizAttemptPurchaseConfirmedComponent implements OnInit, OnDestroy {
  private route = inject(ActivatedRoute);
  private router = inject(Router);
  private quizAttemptService = inject(QuizAttemptService);
  private quizAttemptPurchaseService = inject(QuizAttemptPurchaseService);

  public sessionId = this.route.snapshot.paramMap.get('sessionId') ?? '';
  public purchaseId = this.route.snapshot.queryParamMap.get('purchaseId') ?? '';
  public loading = signal<boolean>(true);

  private pollSubscription?: Subscription;

  public ngOnInit(): void {
    // Poll the purchase confirmation until it can be fetched. A pending purchase answers 404, which
    // we swallow so the timer keeps emitting; the first successful fetch ends the polling.
    this.pollSubscription = timer(0, pollIntervalMs).pipe(
      switchMap(() => this.quizAttemptPurchaseService.getConfirmation(this.sessionId, this.purchaseId)
        .pipe(catchError(() => of(undefined)))),
      filter(confirmation => !!confirmation),
      take(1)
    ).subscribe(() => {
      this.loading.set(false);
      this.startAttempt();
    });
  }

  public ngOnDestroy(): void {
    this.pollSubscription?.unsubscribe();
  }

  private startAttempt(): void {
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
