import { Component, computed, inject, signal } from '@angular/core';
import { ActivatedRoute, Router } from '@angular/router';
import { catchError, of } from 'rxjs';
import { SinglepickComponent } from '../singlepick/singlepick.component';
import { QuizAttemptService } from '../services/quiz-attemptservice';
import { NgIf } from '@angular/common';

const noEvaluation = -1;

@Component({
  selector: 'quizzler-quiz-attempt-step',
  standalone: true,
  imports: [SinglepickComponent, NgIf],
  templateUrl: './quiz-attempt-step.component.html',
  styleUrl: './quiz-attempt-step.component.css'
})
export class QuizAttemptStepComponent {
  private route = inject(ActivatedRoute);
  private router = inject(Router);
  private quizAttemptService = inject(QuizAttemptService);

  public sessionId = this.route.snapshot.paramMap.get('sessionId') ?? '';
  public attemptId = this.route.snapshot.paramMap.get('attemptId') ?? '';
  public questionId = signal(Number(this.route.snapshot.paramMap.get('questionId')));
  public correctOption = signal(noEvaluation);
  public isEvaluated = computed(() => this.correctOption() !== noEvaluation);

  public onAnswerSubmitted(selectedOptionId: number): void {
    this.quizAttemptService.submitAnswer(this.sessionId, this.attemptId, this.questionId(), selectedOptionId)
      .pipe(catchError(err => {
        console.error(err?.message ?? err);
        this.router.navigate(['/error']);
        return of(undefined);
      }))
      .subscribe(answer => {
        if (answer) {
          this.correctOption.set(answer.correctOptionId);
        }
      });
  }

  public onNext(): void {
    this.quizAttemptService.getAttempt(this.sessionId, this.attemptId)
      .pipe(catchError(err => {
        console.error(err?.message ?? err);
        this.router.navigate(['/error']);
        return of(undefined);
      }))
      .subscribe(attempt => {
        if (attempt) {
          this.questionId.set(attempt.questionId);
          this.correctOption.set(noEvaluation);
        }
      });
  }
}
