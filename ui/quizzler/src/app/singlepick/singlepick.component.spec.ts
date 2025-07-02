import { ComponentFixture, TestBed } from '@angular/core/testing';
import { SinglepickComponent } from './singlepick.component';
import { QuestionService } from '../services/questionservice';
import { SinglePickQuestion } from '../entities/singlepickquestion';
import { SingePickOption } from '../entities/singlepickoption';
import { SinglePickResult } from '../entities/singlepickresult';
import { By } from '@angular/platform-browser';

const availableOptions = [1, 2, 3, 4];
describe('SinglepickComponent', () => {
  let component: SinglepickComponent;
  let fixture: ComponentFixture<SinglepickComponent>;

  it ('should display initally all options', async () => {
    const mockQuestionService = returnUntouchedOptions();
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
    const selectedOption = component.singlePickForm.get('selectedOption');
    expect(selectedOption).toBeTruthy();
    expectNumberOfRenderedOptionsIs(4);
  });
  it ('should select no option by default', async () => {
    const mockQuestionService = returnUntouchedOptions();
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
    const selectedOption = component.singlePickForm.get('selectedOption');
    expect(selectedOption).toBeTruthy();
    expect(selectedOption?.value).toEqual('');
  });
  it.each(availableOptions) ('should select first option on first click', (firstSelect) => {
    const mockQuestionService = returnUntouchedOptions();
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

    const selectedOption = component.singlePickForm.get('selectedOption');
    expect(selectedOption).toBeTruthy();
    expect(selectedOption?.value).toEqual(firstSelect);
  });
  it.each(availableOptions) ('should display wrong and correct options in corresponding style after evaluation', (correctOptionId) => {
    const mockQuestionService = returnUntouchedOptions();
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
    fixture.detectChanges();

    querySubmitButton(fixture).click();
    fixture.detectChanges();

    const selectedOption = component.singlePickForm.get('selectedOption');
    expect(selectedOption).toBeTruthy();
    expectOptionIsCorrectWithId(fixture, correctOptionId);
    expectWrongOptionsAreFalseO(availableOptions, correctOptionId, fixture);
  });
  it ('should disable submit button in case of no option selected', async () => {
    const mockQuestionService = returnUntouchedOptions();
    await TestBed.configureTestingModule({
      imports: [SinglepickComponent],
      providers: [
        {provide: QuestionService, useValue: mockQuestionService}
      ]
    }).compileComponents();
    fixture = TestBed.createComponent(SinglepickComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();

    const submitBtn = querySubmitButton(fixture);
    
    expect(submitBtn.disabled).toBeTruthy();
  });
});

function querySubmitButton(fixture: ComponentFixture<SinglepickComponent>) {
  return fixture.debugElement.query(By.css('button[type=submit]')).nativeElement;
}

function expectWrongOptionsAreFalseO(options: number[], correctOptionId: number, fixture: ComponentFixture<SinglepickComponent>) {
  options.filter(option => option != correctOptionId).forEach(option => {
    const falseElement = fixture.debugElement.query(By.css(`#optcontainer-${option}`));
    expect(falseElement.classes['false']).toBeTruthy();
    expect(falseElement.classes['correct']).toBeFalsy();
  });
}

function expectOptionIsCorrectWithId(fixture: ComponentFixture<SinglepickComponent>, correctOptionId: number) {
  const correctElement = fixture.debugElement.query(By.css(`#optcontainer-${correctOptionId}`));
  expect(correctElement.classes['correct']).toBeTruthy();
  expect(correctElement.classes['false']).toBeFalsy();
}

function expectNumberOfRenderedOptionsIs(numberOfExpectedOptions: number) {
  for (let i = 1; i <= numberOfExpectedOptions; i++) {
    const radio = document.getElementById(`${i}`);
    expect(radio).toBeTruthy();
  }
}
function returnUntouchedOptions() : QuestionService {
  const mock = new QuestionService();
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
  const radio = document.getElementById(id);
  expect(radio).toBeTruthy();
  radio?.click();
}