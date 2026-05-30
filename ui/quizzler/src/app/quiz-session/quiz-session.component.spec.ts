import { ComponentFixture, TestBed } from '@angular/core/testing';
import { ActivatedRoute, Router, convertToParamMap } from '@angular/router';
import { of, tap, throwError } from 'rxjs';
import { QuizSessionComponent } from './quiz-session.component';
import { SessionService } from '../services/quiz-sessionservice';
import { QuizAttemptPurchaseService } from '../services/quiz-attempt-purchaseservice';
import { QuizSession } from '../entities/quizsession';
import { QuizAttemptPurchase } from '../entities/quizattemptpurchase';

const SESSION_ID = '33d24a21-3f56-42c6-a959-6567ca56139e';
const PURCHASE_ID = '99999999-8888-7777-6666-555555555555';

describe('QuizSessionComponent', () => {
  let fixture: ComponentFixture<QuizSessionComponent>;
  let component: QuizSessionComponent;
  let mockRouter: { navigate: jest.Mock };
  let mockQuizAttemptPurchaseService: { createPurchase: jest.Mock } | undefined;

  beforeEach(() => {
    mockQuizAttemptPurchaseService = undefined;
  });

  it('loadSession_when_session_exists_then_buy_now_button_is_rendered', async () => {
    const loaded = new QuizSession(SESSION_ID);
    const mockSessionService = sessionServiceReturning(loaded);

    setupFixtureWith(mockSessionService);
    await fixture.whenStable();

    expect(component.quizSession()).toEqual(loaded);
    expect(component.isNotFound()).toBe(false);
    expect(component.isLoading()).toBe(false);
    expect(queryBuyNowButton(fixture)).toBeTruthy();
    expect(queryNotFoundMessage(fixture)).toBeFalsy();
  });

  it('loadSession_when_session_not_found_then_404_message_is_rendered_and_buy_now_button_is_hidden', () => {
    const mockSessionService = sessionServiceReturning(QuizSession.getDefaultQuizSession());

    setupFixtureWith(mockSessionService);

    expect(component.isNotFound()).toBe(true);
    expect(queryNotFoundMessage(fixture)).toBeTruthy();
    expect(queryBuyNowButton(fixture)).toBeFalsy();
  });

  it('loadSession_when_service_throws_then_redirects_to_error', () => {
    const error = { message: 'boom', status: 500 };
    const mockSessionService = sessionServiceThrowing(error);

    setupFixtureWith(mockSessionService);

    expect(mockRouter.navigate).toHaveBeenCalledWith(['/error']);
  });

  it('loadSession_when_service_processes_then_shows_loading_indicator', async () => {
    let isLoadingAfterFetch: boolean | undefined;
    const loaded = new QuizSession(SESSION_ID);
    const mockSessionService = {
      getSessionById: jest.fn().mockReturnValue(of(loaded).pipe(
        tap(() => {
          isLoadingAfterFetch = component.isLoading();
          fixture.detectChanges();
          expect(queryLoadingMessage(fixture)).toBeTruthy();
        })))
    } as unknown as SessionService;

    setupFixtureWith(mockSessionService);
    await fixture.whenStable();

    expect(isLoadingAfterFetch).toBe(true);
    expect(component.isLoading()).toBe(false);
    expect(queryNotFoundMessage(fixture)).toBeFalsy();
    expect(component.quizSession()).toEqual(loaded);
    expect(queryBuyNowButton(fixture)).toBeTruthy();
    expect(queryLoadingMessage(fixture)).toBeFalsy();
  });

  it('onBuyNow_when_purchase_created_then_navigates_to_quiz_attempt_purchase', async () => {
    const loaded = new QuizSession(SESSION_ID);
    const purchase = new QuizAttemptPurchase(PURCHASE_ID, SESSION_ID);
    mockQuizAttemptPurchaseService = { createPurchase: jest.fn().mockReturnValue(of(purchase)) };
    setupFixtureWith(sessionServiceReturning(loaded));
    await fixture.whenStable();

    queryBuyNowButton(fixture).nativeElement.click();

    expect(mockQuizAttemptPurchaseService.createPurchase).toHaveBeenCalledWith(SESSION_ID);
    expect(mockRouter.navigate).toHaveBeenCalledWith(
      ['/quiz-session', SESSION_ID, 'quiz-attempt-purchase', PURCHASE_ID]);
  });

  it('onBuyNow_when_purchase_creation_throws_then_redirects_to_error', async () => {
    const loaded = new QuizSession(SESSION_ID);
    mockQuizAttemptPurchaseService = { createPurchase: jest.fn().mockReturnValue(throwError(() => ({ message: 'boom' }))) };
    setupFixtureWith(sessionServiceReturning(loaded));
    await fixture.whenStable();

    queryBuyNowButton(fixture).nativeElement.click();

    expect(mockRouter.navigate).toHaveBeenCalledWith(['/error']);
  });

  function setupFixtureWith(mockSessionService: SessionService): void {
    mockRouter = { navigate: jest.fn().mockResolvedValue(true) };
    mockQuizAttemptPurchaseService ??= { createPurchase: jest.fn().mockReturnValue(of(new QuizAttemptPurchase(PURCHASE_ID, SESSION_ID))) };
    TestBed.configureTestingModule({
      imports: [QuizSessionComponent],
      providers: [
        { provide: SessionService, useValue: mockSessionService },
        { provide: QuizAttemptPurchaseService, useValue: mockQuizAttemptPurchaseService },
        { provide: Router, useValue: mockRouter },
        {
          provide: ActivatedRoute,
          useValue: { snapshot: { paramMap: convertToParamMap({ sessionId: SESSION_ID }) } }
        }
      ]
    }).compileComponents();
    fixture = TestBed.createComponent(QuizSessionComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  }
});

function sessionServiceReturning(session: QuizSession): SessionService {
  return {
    getSessionById: jest.fn().mockReturnValue(of(session))
  } as unknown as SessionService;
}

function sessionServiceThrowing(error: unknown): SessionService {
  return {
    getSessionById: jest.fn().mockReturnValue(throwError(() => error))
  } as unknown as SessionService;
}

function queryBuyNowButton(fixture: ComponentFixture<QuizSessionComponent>) {
  return fixture.debugElement.query(el => el.name === 'button' && el.nativeElement.textContent.toLowerCase().includes('buy'));
}

function queryNotFoundMessage(fixture: ComponentFixture<QuizSessionComponent>) {
  return fixture.debugElement.query(
      el => el.name === 'p' &&
      el.nativeElement.textContent.toLowerCase().includes('not found')
  );
}

function queryLoadingMessage(fixture: ComponentFixture<QuizSessionComponent>) {
  return fixture.debugElement.query(
    el => el.name === 'p' &&
    el.nativeElement.textContent.toLowerCase().includes('loading')
  );
}
