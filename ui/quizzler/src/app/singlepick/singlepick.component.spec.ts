import { ComponentFixture, fakeAsync, TestBed, tick, waitForAsync } from '@angular/core/testing';
import { SinglepickComponent } from './singlepick.component';
import { QuestionService } from '../services/questionservice';
import { SinglePickQuestion } from '../entities/singlepickquestion';
import { SingePickOption } from '../entities/singlepickoption';
import { FormArray, FormGroup } from '@angular/forms';
import { SinglePickResult } from '../entities/singlepickresult';
import { By } from '@angular/platform-browser';
import { LetDeclaration } from '@angular/compiler';

const availableOptions = [1, 2, 3, 4];
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
  it.each(availableOptions) ('should select first option on first click', (firstSelect) => {
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
  it.each(availableOptions) ('should display wrong and correct options in corresponding style after evaluation', (correctOptionId) => {
    let mockQuestionService = returnUntouchedOptions();
    returnCorrectOptionAt(mockQuestionService, correctOptionId);
    TestBed.configureTestingModule({
      imports: [SinglepickComponent],
      providers: [
        {provide: QuestionService, useValue: mockQuestionService}
      ]
    }).compileComponents();
    fixture = TestBed.createComponent(SinglepickComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();

    selectOptionById(correctOptionId.toString());
    fixture.debugElement.query(By.css('button[type="submit"]')).nativeElement.click();
    fixture.detectChanges();

    let selectedOption = component.singlePickForm.get('selectedOption');
    expect(selectedOption).toBeTruthy();
    expectOptionIsCorrectWithId(fixture, correctOptionId);
    expectWrongOptionsAreFalseO(availableOptions, correctOptionId, fixture);
  });
});
function expectWrongOptionsAreFalseO(options: number[], correctOptionId: number, fixture: ComponentFixture<SinglepickComponent>) {
  options.filter(option => option != correctOptionId).forEach(option => {
    let falseElement = fixture.debugElement.query(By.css(`#optcontainer-${option}`));
    expect(falseElement.classes['false']).toBeTruthy();
    expect(falseElement.classes['correct']).toBeFalsy();
  });
}

function expectOptionIsCorrectWithId(fixture: ComponentFixture<SinglepickComponent>, correctOptionId: number) {
  let correctElement = fixture.debugElement.query(By.css(`#optcontainer-${correctOptionId}`));
  expect(correctElement.classes['correct']).toBeTruthy();
  expect(correctElement.classes['false']).toBeFalsy();
}

function expectNumberOfRenderedOptionsIs(numberOfExpectedOptions: number) {
  for (let i = 1; i <= numberOfExpectedOptions; i++) {
    let radio = document.getElementById(`${i}`);
    expect(radio).toBeTruthy();
  }
}
function returnUntouchedOptions() : QuestionService {
  let mock = new QuestionService();
  mock.getSinglePickQuestionById = jest.fn().mockReturnValue(new SinglePickQuestion(
      "Question ES 1",
      "This is the text of a single pick question !",
      [
        new SingePickOption(1, 'Option 1'),
        new SingePickOption(2, 'Option 2'),
        new SingePickOption(3, 'Option 3'),
        new SingePickOption(4, 'Option 4')
      ]));
    return mock;
  ;
}
function returnCorrectOptionAt(mock: QuestionService, correctOptionId: number) {
   mock.evaluate = jest.fn().mockReturnValue(
    new SinglePickResult(correctOptionId));
}
function selectOptionById(id: string) {
  let radio = document.getElementById(id);
  expect(radio).toBeTruthy();
  radio?.click();
}