import { Component, Input, signal, Signal } from '@angular/core';
import { ActivatedRoute } from '@angular/router';

@Component({
  selector: 'quizzler-quiz-attempt-step-final',
  standalone: true,
  imports: [],
  templateUrl: './quiz-attempt-step-final.component.html',
  styleUrl: './quiz-attempt-step-final.component.css'
})
export class QuizAttemptStepFinalComponent {
  
  public sessionId = this.route.snapshot.paramMap.get('sessionId') ?? '';
  public attemptId = this.route.snapshot.paramMap.get('attemptId') ?? '';

  constructor(private route: ActivatedRoute) { }
}
