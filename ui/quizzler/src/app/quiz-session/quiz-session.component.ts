import { Component, Signal, computed, inject } from '@angular/core';
import { ActivatedRoute } from '@angular/router';
import { of } from 'rxjs';
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
  id: string | null = this.route.snapshot.paramMap.get('id');

  public quizSession = toSignal(
    this.id ? this.sessionService.getSessionById(this.id) : of(QuizSession.getDefaultQuizSession()) 
  );

  public isNotFound = computed(() => this.quizSession()?.isDefault() ?? true);
}
