import { Component, computed, inject } from '@angular/core';
import { ActivatedRoute, Router } from '@angular/router';
import { catchError, of } from 'rxjs';
import { toSignal } from '@angular/core/rxjs-interop';
import { QuizSession } from '../entities/quizsession';
import { SessionService } from '../services/quiz-sessionservice';

@Component({
  selector: 'quizzler-quiz-session',
  standalone: true,
  templateUrl: './quiz-session.component.html',
  styleUrl: './quiz-session.component.css'
})
export class QuizSessionComponent {
  private route = inject(ActivatedRoute);
  private sessionService = inject(SessionService);
  private router = inject(Router);
  id: string | null = this.route.snapshot.paramMap.get('id');

  public quizSession = toSignal(
    (this.id ? this.sessionService.getSessionById(this.id) : of(QuizSession.getDefaultQuizSession()))
      .pipe(catchError(err => {
        console.error(err?.message ?? err);
        this.router.navigate(['/error']);
        return of(undefined);
      }))
  );

  public isNotFound = computed(() => this.quizSession()?.isDefault() ?? true);
}
