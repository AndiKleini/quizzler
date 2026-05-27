import { Component, inject } from '@angular/core';
import { ActivatedRoute } from '@angular/router';

@Component({
  selector: 'quizzler-quiz-attempt-step',
  standalone: true,
  templateUrl: './quiz-attempt-step.component.html',
  styleUrl: './quiz-attempt-step.component.css'
})
export class QuizAttemptStepComponent {
  private route = inject(ActivatedRoute);
  public sessionId = this.route.snapshot.paramMap.get('sessionId');
  public questionId = this.route.snapshot.paramMap.get('questionId');
}
