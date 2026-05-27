import { Component, computed, inject, OnInit, signal } from '@angular/core';
import { ActivatedRoute, Router } from '@angular/router';
import { catchError, map, of, tap } from 'rxjs';
import { QuizSession } from '../entities/quizsession';
import { SessionService } from '../services/quiz-sessionservice';
import { QuizAttemptService } from '../services/quiz-attemptservice';

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
        map(quizSession => 
          new QuizSession(
              quizSession.publicId, 
              quizSession.currentQuestion, 
              quizSession.nextQuestion, 
              quizSession.previousQuestion)),
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
  private quizAttemptService = inject(QuizAttemptService);
  private router = inject(Router);
  private id: string | null = this.route.snapshot.paramMap.get('sessionId');

  public isLoading = signal(true);
  public quizSession = signal(QuizSession.getDefaultQuizSession());
  public isNotFound = computed(() => this.quizSession().isDefault());

  public onStart(): void {
    if (!this.id) {
      return;
    }
    this.quizAttemptService.createAttempt(this.id)
      .pipe(catchError(err => {
        console.error(err?.message ?? err);
        this.router.navigate(['/error']);
        return of(undefined);
      }))
      .subscribe(attempt => {
        if (attempt) {
          this.router.navigate(['/quiz-session', attempt.sessionId, 'attempt-step']);
        }
      });
  }
}
