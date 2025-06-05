import { Component, OnInit } from '@angular/core';
import {FormGroup, ReactiveFormsModule, FormBuilder, FormArray} from '@angular/forms';
import { SinglePickQuestion } from "../entities/singlepickquestion";
import { SingePickOption } from '../entities/singlepickoption';
import { QuestionService } from '../services/questionservice';

@Component({
  selector: 'quizzler-singlepick',
  standalone: true,
  imports: [ ReactiveFormsModule ],
  templateUrl: './singlepick.component.html',
  styleUrl: './singlepick.component.css'
})
export class SinglepickComponent implements OnInit  {
  selectedIndex = -1;
  singlePickForm: FormGroup;
  public singlePickQuestion: SinglePickQuestion;
  constructor(private formBuilder: FormBuilder, questionService: QuestionService) {
    this.singlePickForm = this.formBuilder.group( {
      options: this.formBuilder.array([false])
    });
    this.singlePickQuestion = questionService.getSinglePickQuestionById(1);
  }
    ngOnInit(): void {
      this.singlePickQuestion.options.forEach(
        option => { 
          let optionControl = this.formBuilder.control('');
          optionControl.valueChanges.subscribe(newValue => {
            this.singlePickQuestion.select(option.id);
          });
          this.options.push(optionControl);
        });
    }
    get options() {
      return this.singlePickForm.get('options') as FormArray;
    }
}
