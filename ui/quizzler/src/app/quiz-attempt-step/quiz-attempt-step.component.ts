import { Component, inject } from '@angular/core';
import { ActivatedRoute, Router } from '@angular/router';
import { catchError, of } from 'rxjs';
import { SinglepickComponent } from '../singlepick/singlepick.component';
import { QuizAttemptService } from '../services/quiz-attemptservice';

@Component({
  selector: 'quizzler-quiz-attempt-step',
  standalone: true,
  imports: [SinglepickComponent],
  templateUrl: './quiz-attempt-step.component.html',
  styleUrl: './quiz-attempt-step.component.css'
})
export class QuizAttemptStepComponent {
  private route = inject(ActivatedRoute);
  private router = inject(Router);
  private quizAttemptService = inject(QuizAttemptService);

  public sessionId = this.route.snapshot.paramMap.get('sessionId') ?? '';
  public attemptId = this.route.snapshot.paramMap.get('attemptId') ?? '';
  public questionId = Number(this.route.snapshot.paramMap.get('questionId'));

  public onAnswerSubmitted(selectedOptionId: number): void {
    this.quizAttemptService.submitAnswer(this.sessionId, this.attemptId, this.questionId, selectedOptionId)
      .pipe(catchError(err => {
        console.error(err?.message ?? err);
        this.router.navigate(['/error']);
        return of(undefined);
      }))
      .subscribe();
  }
}