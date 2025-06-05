import { Component, OnInit } from '@angular/core';
import {FormGroup, ReactiveFormsModule, FormBuilder, FormArray} from '@angular/forms';
import { SinglePickQuestion } from "../entities/singlepickquestion";
import { SingePickOption } from '../entities/singlepickoption';

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
  constructor(private formBuilder: FormBuilder) {
    this.singlePickForm = this.formBuilder.group( {
      options: this.formBuilder.array([false])
    });
    this.singlePickQuestion = 
      new SinglePickQuestion(
        "Question ES 1", 
        "This is the text of a single pick question !",
        [ 
          new SingePickOption(1, 'Option 1'), 
          new SingePickOption(2, 'Option 2'),
          new SingePickOption(3, 'Option 3')
        ]);
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
