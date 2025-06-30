import { Component, inject } from '@angular/core';
import { FormGroup, ReactiveFormsModule, FormBuilder } from '@angular/forms';
import { SinglePickQuestion } from "../entities/singlepickquestion";
import { QuestionService } from '../services/questionservice';
import { NgFor, NgClass } from '@angular/common';

@Component({
  selector: 'quizzler-singlepick',
  standalone: true,
  imports: [ ReactiveFormsModule, NgFor, NgClass ],
  templateUrl: './singlepick.component.html',
  styleUrl: './singlepick.component.css'
})
export class SinglepickComponent {
  private formBuilder = inject(FormBuilder);
  private questionService = inject(QuestionService);

  singlePickForm: FormGroup;
  public singlePickQuestion: SinglePickQuestion;
  correctOption = -1;

  constructor() {
    const questionService = this.questionService;

    this.singlePickForm = this.formBuilder.group( {
      selectedOption: ['']
    });
    this.singlePickQuestion = questionService.getSinglePickQuestionById(1);
  }
  submit() {
    const result = this.questionService.evaluate(
        this.singlePickQuestion, 
        this.singlePickForm.get('selectedOption')?.value);
    this.correctOption = result.correctOptionId;
  }
}
