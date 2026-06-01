import { ComponentFixture, TestBed } from '@angular/core/testing';
import { SinglepickComponent } from './singlepick.component';
import { QuestionService } from '../services/questionservice';
import { SinglePickQuestion } from '../entities/singlepickquestion';
import { SingePickOption } from '../entities/singlepickoption';
import { By } from '@angular/platform-browser';
import { of } from 'rxjs';

const QUESTION_ID = 7;
const availableOptions = [1, 2, 3, 4];

describe('SinglepickComponent', () => {
  let fixture: ComponentFixture<SinglepickComponent>;
  let component: SinglepickComponent;

  async function setup(correctOption?: number): Promise<void> {
    TestBed.configureTestingModule({
      imports: [SinglepickComponent],
      providers: [{ provide: QuestionService, useValue: questionServiceReturningOptions() }]
    }).compileComponents();
    fixture = TestBed.createComponent(SinglepickComponent);
    component = fixture.componentInstance;
    fixture.componentRef.setInput('questionId', QUESTION_ID);
    if (correctOption !== undefined) {
      fixture.componentRef.setInput('correctOption', correctOption);
    }
    fixture.detectChanges();
    await fixture.whenStable();
    fixture.detectChanges();
  }

  it('render_when_question_loaded_then_all_options_are_displayed', async () => {
    await setup();

    expect(component.singlePickForm).toBeTruthy();
    availableOptions.forEach(id => expect(document.getElementById(`${id}`)).toBeTruthy());
  });

  it('render_default_then_no_option_is_selected', async () => {
    await setup();

    expect(component.singlePickForm.get('selectedOption')?.value).toEqual('');
  });

  it.each(availableOptions)('selectOption_when_clicked_then_form_value_matches', async (optionId) => {
    await setup();

    selectOptionById(optionId.toString());

    expect(component.singlePickForm.get('selectedOption')?.value).toEqual(optionId);
  });

  it.each(availableOptions)('submit_when_option_selected_then_emits_selected_option', async (optionId) => {
    await setup();
    let emitted: number | undefined;
    component.answerSubmitted.subscribe(value => (emitted = value));
    selectOptionById(optionId.toString());
    fixture.detectChanges();

    querySubmitButton(fixture).click();

    expect(emitted).toEqual(optionId);
  });

  it('submit_when_no_option_selected_then_button_is_disabled', async () => {
    await setup();

    expect(querySubmitButton(fixture).disabled).toBeTruthy();
  });

  it.each(availableOptions)('render_when_correct_option_provided_then_displays_correct_and_false_options', async (correctOptionId) => {
    await setup(correctOptionId);

    const correctElement = fixture.debugElement.query(By.css(`#optcontainer-${correctOptionId}`));
    expect(correctElement.classes['correct']).toBeTruthy();
    expect(correctElement.classes['false']).toBeFalsy();
    availableOptions.filter(o => o !== correctOptionId).forEach(o => {
      const falseElement = fixture.debugElement.query(By.css(`#optcontainer-${o}`));
      expect(falseElement.classes['false']).toBeTruthy();
      expect(falseElement.classes['correct']).toBeFalsy();
    });
  });

  it('render_when_evaluated_and_answer_correct_then_submit_replaced_by_green_correct_label', async () => {
    await setup();
    selectOptionById('2');
    fixture.detectChanges();
    fixture.componentRef.setInput('correctOption', 2);
    fixture.detectChanges();

    expect(fixture.debugElement.query(By.css('button[type=submit]'))).toBeFalsy();
    const label = fixture.debugElement.query(By.css('.question-evaluated-label'));
    expect(label).toBeTruthy();
    expect(label.nativeElement.textContent.trim()).toEqual('Correct');
    expect(label.classes['message-correct']).toBeTruthy();
    expect(label.classes['message-error']).toBeFalsy();
  });

  it('render_when_evaluated_and_answer_wrong_then_submit_replaced_by_red_wrong_label', async () => {
    await setup();
    selectOptionById('2');
    fixture.detectChanges();
    fixture.componentRef.setInput('correctOption', 3);
    fixture.detectChanges();

    expect(fixture.debugElement.query(By.css('button[type=submit]'))).toBeFalsy();
    const label = fixture.debugElement.query(By.css('.question-evaluated-label'));
    expect(label).toBeTruthy();
    expect(label.nativeElement.textContent.trim()).toEqual('Wrong');
    expect(label.classes['message-error']).toBeTruthy();
    expect(label.classes['message-correct']).toBeFalsy();
  });
});

function querySubmitButton(fixture: ComponentFixture<SinglepickComponent>) {
  return fixture.debugElement.query(By.css('button[type=submit]')).nativeElement;
}

function questionServiceReturningOptions(): QuestionService {
  return {
    getSinglePickQuestionById: jest.fn().mockReturnValue(of(new SinglePickQuestion(
      QUESTION_ID,
      'Question ES 1',
      'This is the text of a single pick question !',
      [
        new SingePickOption(1, 'Option 1'),
        new SingePickOption(2, 'Option 2'),
        new SingePickOption(3, 'Option 3'),
        new SingePickOption(4, 'Option 4')
      ])))
  } as unknown as QuestionService;
}

function selectOptionById(id: string) {
  const radio = document.getElementById(id);
  expect(radio).toBeTruthy();
  radio?.click();
}
