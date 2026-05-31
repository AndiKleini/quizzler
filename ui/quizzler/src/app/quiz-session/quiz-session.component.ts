import { Component, computed, inject, OnInit, signal } from '@angular/core';
import { ActivatedRoute, Router } from '@angular/router';
import { catchError, map, of } from 'rxjs';
import { QuizSession } from '../entities/quizsession';
import { SessionService } from '../services/quiz-sessionservice';
import { QuizAttemptPurchaseService } from '../services/quiz-attempt-purchaseservice';

@Component({
  selector: 'quizzler-quiz-session',
  standalone: true,
  templateUrl: './quiz-session.component.html',
  styleUrl: './quiz-session.component.css'
})
export class QuizSessionComponent implements OnInit {

  ngOnInit(): void {
    (this.id ? this.sessionService.getSessionById(this.id) : of(QuizSession.getDefaultQuizSession()))
      .pipe(
        map(quizSession => new QuizSession(quizSession.publicId)),
        catchError(err => {
          console.error(err?.message ?? err);
          this.isLoading.set(false);
          this.router.navigate(['/error']);
          return of(undefined);
      })).subscribe(session => {
        this.isLoading.set(false);
        this.quizSession.set(session ?? QuizSession.getDefaultQuizSession());
      });
  }

  private route = inject(ActivatedRoute);
  private sessionService = inject(SessionService);
  private quizAttemptPurchaseService = inject(QuizAttemptPurchaseService);
  private router = inject(Router);
  private id: string | null = this.route.snapshot.paramMap.get('sessionId');

  public isLoading = signal(true);
  public quizSession = signal(QuizSession.getDefaultQuizSession());
  public isNotFound = computed(() => this.quizSession().isDefault());

  public onBuyNow(): void {
    if (!this.id) {
      return;
    }
    this.quizAttemptPurchaseService.createPurchase(this.id)
      .pipe(catchError(err => {
        console.error(err?.message ?? err);
        this.router.navigate(['/error']);
        return of(undefined);
      }))
      .subscribe(purchase => {
        if (purchase) {
          this.router.navigate(
            ['/quiz-session', purchase.sessionId, 'quiz-attempt-purchase', purchase.purchaseId],
            { state: { price: purchase.price } });
        }
      });
  }
}
