import { ComponentFixture, TestBed } from '@angular/core/testing';
import { ActivatedRoute, Router, convertToParamMap } from '@angular/router';
import { of, tap, throwError } from 'rxjs';
import { QuizSessionComponent } from './quiz-session.component';
import { SessionService } from '../services/quiz-sessionservice';
import { QuizAttemptService } from '../services/quiz-attemptservice';
import { QuizSession } from '../entities/quizsession';
import { QuizAttempt } from '../entities/quizattempt';

const SESSION_ID = '33d24a21-3f56-42c6-a959-6567ca56139e';
const ATTEMPT_ID = 'aaaaaaaa-bbbb-cccc-dddd-eeeeeeeeeeee';
const QUESTION_ID = 42;

describe('QuizSessionComponent', () => {
  let fixture: ComponentFixture<QuizSessionComponent>;
  let component: QuizSessionComponent;
  let mockRouter: { navigate: jest.Mock };
  let mockQuizAttemptService: { createAttempt: jest.Mock } | undefined;

  beforeEach(() => {
    mockQuizAttemptService = undefined;
  });

  it('loadSession_when_session_exists_then_start_button_is_rendered', async () => {
    const loaded = new QuizSession(SESSION_ID);
    const mockSessionService = sessionServiceReturning(loaded);

    setupFixtureWith(mockSessionService);
    await fixture.whenStable();

    expect(component.quizSession()).toEqual(loaded);
    expect(component.isNotFound()).toBe(false);
    expect(component.isLoading()).toBe(false);
    expect(queryStartButton(fixture)).toBeTruthy();
    expect(queryNotFoundMessage(fixture)).toBeFalsy();
  });

  it('loadSession_when_session_not_found_then_404_message_is_rendered_and_start_button_is_hidden', () => {
    const mockSessionService = sessionServiceReturning(QuizSession.getDefaultQuizSession());

    setupFixtureWith(mockSessionService);

    expect(component.isNotFound()).toBe(true);
    expect(queryNotFoundMessage(fixture)).toBeTruthy();
    expect(queryStartButton(fixture)).toBeFalsy();
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
    expect(queryStartButton(fixture)).toBeTruthy();
    expect(queryLoadingMessage(fixture)).toBeFalsy();
  });

  it('onStart_when_attempt_created_then_navigates_to_attempt_step', async () => {
    const loaded = new QuizSession(SESSION_ID);
    const attempt = new QuizAttempt(ATTEMPT_ID, SESSION_ID, QUESTION_ID);
    mockQuizAttemptService = { createAttempt: jest.fn().mockReturnValue(of(attempt)) };
    setupFixtureWith(sessionServiceReturning(loaded));
    await fixture.whenStable();

    queryStartButton(fixture).nativeElement.click();

    expect(mockQuizAttemptService.createAttempt).toHaveBeenCalledWith(SESSION_ID);
    expect(mockRouter.navigate).toHaveBeenCalledWith(
      ['/quiz-session', SESSION_ID, 'attempt', ATTEMPT_ID, 'question', QUESTION_ID]);
  });

  it('onStart_when_attempt_creation_throws_then_redirects_to_error', async () => {
    const loaded = new QuizSession(SESSION_ID);
    mockQuizAttemptService = { createAttempt: jest.fn().mockReturnValue(throwError(() => ({ message: 'boom' }))) };
    setupFixtureWith(sessionServiceReturning(loaded));
    await fixture.whenStable();

    queryStartButton(fixture).nativeElement.click();

    expect(mockRouter.navigate).toHaveBeenCalledWith(['/error']);
  });

  function setupFixtureWith(mockSessionService: SessionService): void {
    mockRouter = { navigate: jest.fn().mockResolvedValue(true) };
    mockQuizAttemptService ??= { createAttempt: jest.fn().mockReturnValue(of(new QuizAttempt(ATTEMPT_ID, SESSION_ID, QUESTION_ID))) };
    TestBed.configureTestingModule({
      imports: [QuizSessionComponent],
      providers: [
        { provide: SessionService, useValue: mockSessionService },
        { provide: QuizAttemptService, useValue: mockQuizAttemptService },
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

function queryStartButton(fixture: ComponentFixture<QuizSessionComponent>) {
  return fixture.debugElement.query(el => el.name === 'button' && el.nativeElement.textContent.toLowerCase().includes('start'));
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