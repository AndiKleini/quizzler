import { Component, computed, inject, input, output } from '@angular/core';
import { toObservable, toSignal } from '@angular/core/rxjs-interop';
import {
  FormGroup,
  ReactiveFormsModule,
  FormBuilder,
  ValidatorFn,
  AbstractControl,
  ValidationErrors } from '@angular/forms';
import { switchMap } from 'rxjs';
import { QuestionService } from '../services/questionservice';
import { NgFor, NgIf, NgClass } from '@angular/common';

const optionDefaultValue = '';
const noEvaluation = -1;

@Component({
  selector: 'quizzler-singlepick',
  standalone: true,
  imports: [ ReactiveFormsModule, NgFor, NgIf, NgClass ],
  templateUrl: './singlepick.component.html',
  styleUrl: './singlepick.component.css'
})
export class SinglepickComponent {
  private formBuilder = inject(FormBuilder);
  private questionService = inject(QuestionService);

  public questionId = input.required<number>();
  public correctOption = input<number>(noEvaluation);
  public answerSubmitted = output<number>();

  public singlePickQuestion = toSignal(
    toObservable(this.questionId).pipe(
      switchMap(id => this.questionService.getSinglePickQuestionById(id))
    )
  );
  public isEvaluated = computed(() => this.correctOption() !== noEvaluation);
  public singlePickForm: FormGroup = this.formBuilder.group({
    selectedOption: [optionDefaultValue, [this.anyOptionSelectedValidator()]]
  });

  submit() {
    if (!this.singlePickQuestion()) {
      return;
    }
    const selectedOptionId = Number(this.singlePickForm.get('selectedOption')?.value);
    this.answerSubmitted.emit(selectedOptionId);
  }

  private anyOptionSelectedValidator(): ValidatorFn {
    return (control: AbstractControl): ValidationErrors | null => {
      return control.value === optionDefaultValue ? { errNoOptionSelected: true } : null;
    };
  }
}
