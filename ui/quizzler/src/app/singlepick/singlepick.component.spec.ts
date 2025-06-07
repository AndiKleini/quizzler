import { ComponentFixture, TestBed } from '@angular/core/testing';
import { SinglepickComponent } from './singlepick.component';
import { QuestionService } from '../services/questionservice';
import { SinglePickQuestion } from '../entities/singlepickquestion';
import { SingePickOption } from '../entities/singlepickoption';
import { FormArray } from '@angular/forms';

describe('SinglepickComponent', () => {
  let component: SinglepickComponent;
  let fixture: ComponentFixture<SinglepickComponent>;

  it ('should display initally all options', async () => {
    let mockQuestionService = {
       getSinglePickQuestionById: jest.fn().mockReturnValue( new SinglePickQuestion(
                   "Question ES 1", 
                   "This is the text of a single pick question !",
                   [ 
                     new SingePickOption(1, 'Option 1'), 
                     new SingePickOption(2, 'Option 2'),
                     new SingePickOption(3, 'Option 3'),
                     new SingePickOption(4, 'Option 4')
                   ]))
    };
    await TestBed.configureTestingModule({
      imports: [SinglepickComponent],
      providers: [
        {provide: QuestionService, useValue: mockQuestionService}
      ]
    }).compileComponents();
    fixture = TestBed.createComponent(SinglepickComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();

    expect(component.singlePickForm).toBeTruthy();
    let options = component.singlePickForm.get('options');
    expect(options).toBeTruthy();
    expect((options as FormArray).length).toEqual(4);
  });
});
