import { Component, OnInit } from '@angular/core';
import { FormGroup, ReactiveFormsModule, FormBuilder } from '@angular/forms';
import { SinglePickQuestion } from "../entities/singlepickquestion";
import { SingePickOption } from '../entities/singlepickoption';
import { QuestionService } from '../services/questionservice';
import { NgFor, NgClass } from '@angular/common';

@Component({
  selector: 'quizzler-singlepick',
  standalone: true,
  imports: [ ReactiveFormsModule, NgFor, NgClass ],
  templateUrl: './singlepick.component.html',
  styleUrl: './singlepick.component.css'
})
export class SinglepickComponent implements OnInit  {
  singlePickForm: FormGroup;
  public singlePickQuestion: SinglePickQuestion;
selectedIndex: any;
  constructor(private formBuilder: FormBuilder, questionService: QuestionService) {
    this.singlePickForm = this.formBuilder.group( {
      selectedOption: ['']
    });
    this.singlePickQuestion = questionService.getSinglePickQuestionById(1);
  }
  ngOnInit(): void {
  }
  submit() {
    console.log(this.singlePickForm.get('selectedOption')?.value);
  }
}
