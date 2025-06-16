import { ComponentFixture, fakeAsync, TestBed, tick, waitForAsync } from '@angular/core/testing';
import { SinglepickComponent } from './singlepick.component';
import { QuestionService } from '../services/questionservice';
import { SinglePickQuestion } from '../entities/singlepickquestion';
import { SingePickOption } from '../entities/singlepickoption';
import { FormArray, FormGroup } from '@angular/forms';

describe('SinglepickComponent', () => {
  let component: SinglepickComponent;
  let fixture: ComponentFixture<SinglepickComponent>;

  it ('should display initally all options', async () => {
    let mockQuestionService = returnUntouchedOptions();
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
    let selectedOption = component.singlePickForm.get('selectedOption');
    expect(selectedOption).toBeTruthy();
    expectNumberOfRenderedOptionsIs(4);
  });
  it ('should select no option by default', async () => {
    let mockQuestionService = returnUntouchedOptions();
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
    let selectedOption = component.singlePickForm.get('selectedOption');
    expect(selectedOption).toBeTruthy();
    expect(selectedOption?.value).toEqual('');
  });
  it.each([1,2,3,4]) ('should select first option on first click', (firstSelect) => {
    let mockQuestionService = returnUntouchedOptions();
    TestBed.configureTestingModule({
      imports: [SinglepickComponent],
      providers: [
        {provide: QuestionService, useValue: mockQuestionService}
      ]
    }).compileComponents();
    fixture = TestBed.createComponent(SinglepickComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();

    selectOptionById(firstSelect.toString());

    let selectedOption = component.singlePickForm.get('selectedOption');
    expect(selectedOption).toBeTruthy();
    expect(selectedOption?.value).toEqual(firstSelect);
  });
});
function expectNumberOfRenderedOptionsIs(numberOfExpectedOptions: number) {
  for (let i = 1; i <= numberOfExpectedOptions; i++) {
    let radio = document.getElementById(`${i}`);
    expect(radio).toBeTruthy();
  }
}
function returnUntouchedOptions() {
  return {
    getSinglePickQuestionById: jest.fn().mockReturnValue(new SinglePickQuestion(
      "Question ES 1",
      "This is the text of a single pick question !",
      [
        new SingePickOption(1, 'Option 1'),
        new SingePickOption(2, 'Option 2'),
        new SingePickOption(3, 'Option 3'),
        new SingePickOption(4, 'Option 4')
      ]))
  };
}
function selectOptionById(id: string) {
  let radio = document.getElementById(id);
  expect(radio).toBeTruthy();
  radio?.click();
}