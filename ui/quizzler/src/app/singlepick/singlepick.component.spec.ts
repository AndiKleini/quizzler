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
    let options = component.singlePickForm.get('options');
    
    expect(options).toBeTruthy();
    expect((options as FormArray).length).toEqual(4);
  });
  it.each([0,1,2,3]) ('should select first option on first click', (firstSelect) => {
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

    expectOnlyOneOptionIsSelected(component.singlePickForm, firstSelect);
  });
  it.each([0,1,2,3]) ('should toggle selection with clicking twice', (firstSelect) => {
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
    selectOptionById(firstSelect.toString());

    expectNoOptionIsSelected(component.singlePickForm);
  });
});
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
function expectOnlyOneOptionIsSelected(form: FormGroup<any>, selectedIndex: number) {
  let options = form.get('options');
  let array = options as FormArray;
  array.controls.forEach((element, index) => { 
    expect(element.value).toEqual(index == selectedIndex); });
}
function selectOptionById(id: string) {
  let checkBox = document.getElementById(id);
  expect(checkBox).toBeTruthy();
  checkBox?.click();
}
function expectNoOptionIsSelected(form: FormGroup<any>) {
  let options = form.get('options');
  let array = options as FormArray;
  array.controls.forEach((element, index) => { 
    expect(element.value).toEqual(false); });
}