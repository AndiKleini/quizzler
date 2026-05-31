import { ComponentFixture, TestBed, fakeAsync, tick } from '@angular/core/testing';
import { ActivatedRoute, Router, convertToParamMap } from '@angular/router';
import { of, throwError } from 'rxjs';

import { QuizAttemptPurchaseConfirmedComponent } from './quiz-attempt-purchase-confirmed.component';
import { QuizAttemptService } from '../services/quiz-attemptservice';
import { QuizAttemptPurchaseService } from '../services/quiz-attempt-purchaseservice';
import { QuizAttempt } from '../entities/quizattempt';
import { QuizAttemptPurchaseConfirmation } from '../entities/quizattemptpurchaseconfirmation';

const SESSION_ID = '33d24a21-3f56-42c6-a959-6567ca56139e';
const PURCHASE_ID = '99999999-8888-7777-6666-555555555555';
const CONFIRMATION_ID = '11111111-2222-3333-4444-555555555555';
const ATTEMPT_ID = 'aaaaaaaa-bbbb-cccc-dddd-eeeeeeeeeeee';
const QUESTION_ID = 42;
const CREATED_AT = '2026-05-30T10:00:00Z';

describe('QuizAttemptPurchaseConfirmedComponent', () => {
  let fixture: ComponentFixture<QuizAttemptPurchaseConfirmedComponent>;
  let component: QuizAttemptPurchaseConfirmedComponent;
  let mockRouter: { navigate: jest.Mock };
  let mockQuizAttemptService: { createAttempt: jest.Mock };
  let mockQuizAttemptPurchaseService: { getConfirmation: jest.Mock };

  function setup(): void {
    mockRouter = { navigate: jest.fn().mockResolvedValue(true) };
    mockQuizAttemptService = {
      createAttempt: jest.fn().mockReturnValue(of(new QuizAttempt(ATTEMPT_ID, SESSION_ID, QUESTION_ID)))
    };
    mockQuizAttemptPurchaseService = {
      getConfirmation: jest.fn().mockReturnValue(throwError(() => ({ status: 404 })))
    };
    TestBed.configureTestingModule({
      imports: [QuizAttemptPurchaseConfirmedComponent],
      providers: [
        { provide: QuizAttemptService, useValue: mockQuizAttemptService },
        { provide: QuizAttemptPurchaseService, useValue: mockQuizAttemptPurchaseService },
        { provide: Router, useValue: mockRouter },
        {
          provide: ActivatedRoute,
          useValue: {
            snapshot: {
              paramMap: convertToParamMap({ sessionId: SESSION_ID }),
              queryParamMap: convertToParamMap({ purchaseId: PURCHASE_ID })
            }
          }
        }
      ]
    }).compileComponents();
    fixture = TestBed.createComponent(QuizAttemptPurchaseConfirmedComponent);
    component = fixture.componentInstance;
  }

  it('reads_session_from_path_and_purchase_from_query_parameter', () => {
    setup();

    expect(component.sessionId).toBe(SESSION_ID);
    expect(component.purchaseId).toBe(PURCHASE_ID);
  });

  it('while_confirmation_pending_then_shows_loading_and_keeps_polling', fakeAsync(() => {
    setup();

    fixture.detectChanges();
    tick(0);
    fixture.detectChanges();

    expect(component.loading()).toBe(true);
    expect(fixture.nativeElement.textContent).toContain('Loading');
    expect(mockQuizAttemptPurchaseService.getConfirmation).toHaveBeenCalledWith(SESSION_ID, PURCHASE_ID);

    tick(5000);
    expect(mockQuizAttemptPurchaseService.getConfirmation).toHaveBeenCalledTimes(2);

    fixture.destroy();
  }));

  it('polls_every_5s_until_confirmation_then_hides_loading_creates_attempt_and_navigates', fakeAsync(() => {
    setup();
    mockQuizAttemptPurchaseService.getConfirmation
      .mockReturnValueOnce(throwError(() => ({ status: 404 })))
      .mockReturnValueOnce(throwError(() => ({ status: 404 })))
      .mockReturnValueOnce(of(new QuizAttemptPurchaseConfirmation(CONFIRMATION_ID, PURCHASE_ID, CREATED_AT)));

    fixture.detectChanges();
    tick(0);
    expect(component.loading()).toBe(true);

    tick(5000);
    expect(component.loading()).toBe(true);

    tick(5000);
    expect(component.loading()).toBe(false);
    expect(mockQuizAttemptPurchaseService.getConfirmation).toHaveBeenCalledTimes(3);
    expect(mockQuizAttemptService.createAttempt).toHaveBeenCalledWith(SESSION_ID, PURCHASE_ID);
    expect(mockRouter.navigate).toHaveBeenCalledWith(
      ['/quiz-session', SESSION_ID, 'attempt', ATTEMPT_ID, 'question', QUESTION_ID]);
  }));

  it('when_attempt_creation_fails_then_redirects_to_error', fakeAsync(() => {
    setup();
    mockQuizAttemptPurchaseService.getConfirmation.mockReturnValue(
      of(new QuizAttemptPurchaseConfirmation(CONFIRMATION_ID, PURCHASE_ID, CREATED_AT)));
    mockQuizAttemptService.createAttempt.mockReturnValue(throwError(() => ({ message: 'boom' })));

    fixture.detectChanges();
    tick(0);

    expect(mockRouter.navigate).toHaveBeenCalledWith(['/error']);
  }));
});
