import { Component, Input } from '@angular/core';
import { AsyncPipe } from '@angular/common';

import { QuestionService } from '../services/question.service';
import { Observable } from 'rxjs';
import { Question } from '../entities/question';

@Component({
  selector: 'app-single-pick',
  standalone: true,
  imports: [ AsyncPipe ],
  templateUrl: './single-pick.component.html',
  styleUrl: './single-pick.component.css'
})
export class SinglePickComponent {
  questionId: number = 0;
  question$?: Observable<Question>;

  constructor(private questionService: QuestionService) {}
   
  @Input()
  set id(questionId: string) {
    this.question$ = this.questionService.getQuestion(parseInt(questionId));
  }   
} 