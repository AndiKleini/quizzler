import { ComponentFixture, TestBed } from '@angular/core/testing';
import { ActivatedRoute, Router, convertToParamMap } from '@angular/router';
import { of, throwError } from 'rxjs';
import { By } from '@angular/platform-browser';

import { QuizAttemptPurchaseComponent } from './quiz-attempt-purchase.component';
import { QuizAttemptService } from '../services/quiz-attemptservice';
import { QuizAttempt } from '../entities/quizattempt';

const SESSION_ID = '33d24a21-3f56-42c6-a959-6567ca56139e';
const ATTEMPT_ID = 'aaaaaaaa-bbbb-cccc-dddd-eeeeeeeeeeee';
const PURCHASE_ID = '99999999-8888-7777-6666-555555555555';
const QUESTION_ID = 42;

describe('QuizAttemptPurchaseComponent', () => {
  let fixture: ComponentFixture<QuizAttemptPurchaseComponent>;
  let component: QuizAttemptPurchaseComponent;
  let mockRouter: { navigate: jest.Mock };
  let mockQuizAttemptService: { createAttempt: jest.Mock };

  function setup(): void {
    mockRouter = { navigate: jest.fn().mockResolvedValue(true) };
    mockQuizAttemptService = {
      createAttempt: jest.fn().mockReturnValue(of(new QuizAttempt(ATTEMPT_ID, SESSION_ID, QUESTION_ID)))
    };
    TestBed.configureTestingModule({
      imports: [QuizAttemptPurchaseComponent],
      providers: [
        { provide: QuizAttemptService, useValue: mockQuizAttemptService },
        { provide: Router, useValue: mockRouter },
        {
          provide: ActivatedRoute,
          useValue: {
            snapshot: {
              paramMap: convertToParamMap({ sessionId: SESSION_ID, purchaseId: PURCHASE_ID })
            }
          }
        }
      ]
    }).compileComponents();
    fixture = TestBed.createComponent(QuizAttemptPurchaseComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  }

  it('onStart_create_attempt_with_purchase_id_and_navigates_to_attempt_step', () => {
    setup();

    queryStartButton()!.click();

    expect(mockQuizAttemptService.createAttempt).toHaveBeenCalledWith(SESSION_ID, PURCHASE_ID);
    expect(mockRouter.navigate).toHaveBeenCalledWith(
      ['/quiz-session', SESSION_ID, 'attempt', ATTEMPT_ID, 'question', QUESTION_ID]);
  });

  it('onStart_when_attempt_creation_throws_then_redirects_to_error', () => {
    setup();
    mockQuizAttemptService.createAttempt.mockReturnValue(throwError(() => ({ message: 'boom' })));

    component.onStart();

    expect(mockRouter.navigate).toHaveBeenCalledWith(['/error']);
  });

  function queryStartButton(): HTMLButtonElement | null {
    const debugEl = fixture.debugElement.query(By.css('button'));
    return debugEl ? debugEl.nativeElement as HTMLButtonElement : null;
  }
});
