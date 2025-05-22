import { Component, Input } from '@angular/core';
import { AsyncPipe, NgFor } from '@angular/common';

import { QuestionService } from '../services/question.service';
import { Observable, map, mergeMap, toArray, tap } from 'rxjs';
import { Question } from '../entities/question';
import { ReactiveFormsModule, FormGroup, FormBuilder, FormControl, FormArray } from '@angular/forms';

@Component({
  selector: 'app-single-pick',
  standalone: true,
  imports: [ AsyncPipe, NgFor, ReactiveFormsModule ],
  templateUrl: './single-pick.component.html',
  styleUrl: './single-pick.component.css'
})
export class SinglePickComponent {
  
  questionId: number = 0;
  question$?: Observable<Question>;
  singlePickQuestionForm!: FormGroup;
  
  constructor(
    private questionService: QuestionService, 
    private formBuilder: FormBuilder) {}

  ngOnInit(): void {
    console.log("In ngOnInit");
    this.buildOptions()?.
    subscribe(
      q => { 
        console.log(`In setting group to ${q.controls.length}`);
        this.singlePickQuestionForm = this.formBuilder.group({ options: q });
      });
  }
        
        @Input()
        set id(questionId: string) {
          this.question$ = this.questionService.getQuestion(parseInt(questionId));
        }
        
        submitquestion(value: any) {
          console.log("In submit question");
          this.question$?.subscribe(s => console.log(value));
        }
        
        selectionchanged(optionIndex: number) {
          console.log(`In Selection State changed. ${optionIndex} \n`);
        }
        
        get options(): FormArray<FormControl> {
          return <FormArray<FormControl>>this.singlePickQuestionForm.get('options');
        };

        buildOptions(): Observable<FormArray<FormControl>> | undefined {
          console.log("In build options");
          return this.question$?.pipe(mergeMap((q,i) => q.options))
            .pipe(map((o,i) => new FormControl(o.selected)))
            .pipe(tap(s => console.log(s.value)))
            .pipe(toArray())
            .pipe(map((a,i) => new FormArray<FormControl>(a)))
            .pipe(tap(a => console.log(a.length)));
        }
      } 