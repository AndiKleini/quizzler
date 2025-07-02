import { Component, inject } from '@angular/core';
import { 
  FormGroup, 
  ReactiveFormsModule, 
  FormBuilder, 
  ValidatorFn, 
  AbstractControl, 
  ValidationErrors } from '@angular/forms';
import { SinglePickQuestion } from "../entities/singlepickquestion";
import { QuestionService } from '../services/questionservice';
import { NgFor, NgClass } from '@angular/common';

const optionDefaultValue = '';

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
      selectedOption: [ 
        optionDefaultValue, 
        [ 
          this.anyOptionSelectedValidator() 
        ] 
      ], 
    });
    this.singlePickQuestion = questionService.getSinglePickQuestionById(1);
  }
  submit() {
    const result = this.questionService.evaluate(
        this.singlePickQuestion, 
        this.singlePickForm.get('selectedOption')?.value);
    this.correctOption = result.correctOptionId;
  }
  anyOptionSelectedValidator(): ValidatorFn {
    return (control:AbstractControl) : ValidationErrors | null => {
      return control.value === optionDefaultValue ? { errNoOptionSelected: true } : null; 
    };
  }
}
