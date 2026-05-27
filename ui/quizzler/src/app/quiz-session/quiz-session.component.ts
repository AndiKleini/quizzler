import { Component, inject, OnInit, signal } from '@angular/core';
import { ActivatedRoute, Router } from '@angular/router';
import { catchError, map, of, tap } from 'rxjs';
import { QuizSession } from '../entities/quizsession';
import { SessionService } from '../services/quiz-sessionservice';

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
        tap(() => this.isLoading.set(false)),
        map(quizsession => 
          new QuizSession(
              quizsession.publicId, 
              quizsession.currentQuestion, 
              quizsession.nextQuestion, 
              quizsession.previousQuestion)),
        catchError(err => {
          console.error(err?.message ?? err);
          this.isLoading.set(false);
          this.router.navigate(['/error']);
          return of(undefined);
      })).subscribe(session => {
        this.quizSession = session ?? QuizSession.getDefaultQuizSession();
        this.isNotFound.set(this.quizSession.isDefault());
      });
  }

  private route = inject(ActivatedRoute);
  private sessionService = inject(SessionService);
  private router = inject(Router);
  private id: string | null = this.route.snapshot.paramMap.get('id');

  public isLoading = signal(true);

  public quizSession = QuizSession.getDefaultQuizSession();

  public isNotFound = signal(false);
}
