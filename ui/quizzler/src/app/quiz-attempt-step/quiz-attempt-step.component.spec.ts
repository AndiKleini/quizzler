import { ComponentFixture, TestBed } from '@angular/core/testing';
import { ActivatedRoute, Router, convertToParamMap } from '@angular/router';
import { of, throwError } from 'rxjs';
import { By } from '@angular/platform-browser';

import { QuizAttemptStepComponent } from './quiz-attempt-step.component';
import { SinglepickComponent } from '../singlepick/singlepick.component';
import { QuestionService } from '../services/questionservice';
import { QuizAttemptService } from '../services/quiz-attemptservice';
import { SinglePickQuestion } from '../entities/singlepickquestion';
import { SingePickOption } from '../entities/singlepickoption';
import { Answer } from '../entities/answer';
import { QuizAttempt } from '../entities/quizattempt';

const SESSION_ID = '33d24a21-3f56-42c6-a959-6567ca56139e';
const ATTEMPT_ID = 'aaaaaaaa-bbbb-cccc-dddd-eeeeeeeeeeee';
const QUESTION_ID = 42;
const NEXT_QUESTION_ID = 43;
const SELECTED_OPTION_ID = 3;
const CORRECT_OPTION_ID = 2;
const NOW = '2026-05-28T10:00:00Z';

describe('QuizAttemptStepComponent', () => {
  let fixture: ComponentFixture<QuizAttemptStepComponent>;
  let component: QuizAttemptStepComponent;
  let mockRouter: { navigate: jest.Mock };
  let mockQuizAttemptService: { createAttempt: jest.Mock; submitAnswer: jest.Mock; getAttempt: jest.Mock };

  function setup(): void {
    mockRouter = { navigate: jest.fn().mockResolvedValue(true) };
    mockQuizAttemptService = {
      createAttempt: jest.fn(),
      submitAnswer: jest.fn().mockReturnValue(of(
        new Answer(1, ATTEMPT_ID, QUESTION_ID, SELECTED_OPTION_ID, CORRECT_OPTION_ID, NOW))),
      getAttempt: jest.fn().mockReturnValue(of(new QuizAttempt(ATTEMPT_ID, SESSION_ID, NEXT_QUESTION_ID)))
    };
    TestBed.configureTestingModule({
      imports: [QuizAttemptStepComponent],
      providers: [
        { provide: QuestionService, useValue: questionServiceReturningOptions() },
        { provide: QuizAttemptService, useValue: mockQuizAttemptService },
        { provide: Router, useValue: mockRouter },
        {
          provide: ActivatedRoute,
          useValue: {
            snapshot: {
              paramMap: convertToParamMap({
                sessionId: SESSION_ID,
                attemptId: ATTEMPT_ID,
                questionId: QUESTION_ID.toString()
              })
            }
          }
        }
      ]
    }).compileComponents();
    fixture = TestBed.createComponent(QuizAttemptStepComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  }

  it('render_when_route_params_present_then_passes_question_id_to_child', () => {
    setup();

    const child = queryChild();
    expect(child.questionId()).toEqual(QUESTION_ID);
  });

  it('render_before_answer_submitted_then_next_button_is_hidden', () => {
    setup();

    expect(queryNextButton()).toBeFalsy();
  });

  it('onAnswerSubmitted_when_child_emits_then_posts_answer_to_attempt_endpoint', () => {
    setup();

    queryChild().answerSubmitted.emit(SELECTED_OPTION_ID);

    expect(mockQuizAttemptService.submitAnswer).toHaveBeenCalledWith(
      SESSION_ID, ATTEMPT_ID, QUESTION_ID, SELECTED_OPTION_ID);
  });

  it('onAnswerSubmitted_when_response_arrives_then_passes_correct_option_to_child_and_shows_next', () => {
    setup();

    queryChild().answerSubmitted.emit(SELECTED_OPTION_ID);
    fixture.detectChanges();

    expect(queryChild().correctOption()).toEqual(CORRECT_OPTION_ID);
    expect(queryNextButton()).toBeTruthy();
  });

  it('onAnswerSubmitted_when_post_throws_then_redirects_to_error', () => {
    setup();
    mockQuizAttemptService.submitAnswer.mockReturnValue(throwError(() => ({ message: 'boom' })));

    component.onAnswerSubmitted(SELECTED_OPTION_ID);

    expect(mockRouter.navigate).toHaveBeenCalledWith(['/error']);
  });

  it('onNext_when_clicked_then_fetches_next_attempt_and_renders_next_question', () => {
    setup();
    queryChild().answerSubmitted.emit(SELECTED_OPTION_ID);
    fixture.detectChanges();

    const button = queryNextButton();
    expect(button).toBeTruthy();
    button!.click();
    fixture.detectChanges();

    expect(mockQuizAttemptService.getAttempt).toHaveBeenCalledWith(SESSION_ID, ATTEMPT_ID);
    expect(queryChild().questionId()).toEqual(NEXT_QUESTION_ID);
    expect(queryNextButton()).toBeFalsy();
  });

  it('onNext_when_get_throws_then_redirects_to_error', () => {
    setup();
    mockQuizAttemptService.getAttempt.mockReturnValue(throwError(() => ({ message: 'boom' })));

    component.onNext();

    expect(mockRouter.navigate).toHaveBeenCalledWith(['/error']);
  });

  function queryChild(): SinglepickComponent {
    return fixture.debugElement.query(By.directive(SinglepickComponent)).componentInstance as SinglepickComponent;
  }

  function queryNextButton(): HTMLButtonElement | null {
    const debugEl = fixture.debugElement.query(By.css('button.next-button'));
    return debugEl ? debugEl.nativeElement as HTMLButtonElement : null;
  }
});

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
