import { ComponentFixture, TestBed } from '@angular/core/testing';
import { ActivatedRoute, convertToParamMap } from '@angular/router';
import { By } from '@angular/platform-browser';
import { of } from 'rxjs';

import { QuizSessionComponent } from './quiz-session.component';
import { SessionService } from '../services/quiz-sessionservice';
import { QuizSession } from '../entities/quizsession';

const SESSION_ID = '33d24a21-3f56-42c6-a959-6567ca56139e';

describe('QuizSessionComponent', () => {
  let fixture: ComponentFixture<QuizSessionComponent>;
  let component: QuizSessionComponent;

  it('loadSession_when_session_exists_then_start_button_is_rendered', () => {
    const loaded = new QuizSession(SESSION_ID, 42, 0, 0);
    const mockSessionService = sessionServiceReturning(loaded);

    setupFixtureWith(mockSessionService);

    expect(component.quizSession()).toEqual(loaded);
    expect(component.isNotFound()).toBe(false);
    expect(queryStartButton(fixture)).toBeTruthy();
    expect(queryNotFoundMessage(fixture)).toBeFalsy();
  });

  it('loadSession_when_session_not_found_then_404_message_is_rendered_and_start_button_is_hidden', () => {
    const mockSessionService = sessionServiceReturning(QuizSession.getDefaultQuizSession());

    setupFixtureWith(mockSessionService);

    expect(component.isNotFound()).toBe(true);
    expect(queryNotFoundMessage(fixture)?.nativeElement.textContent).toContain('not found');
    expect(queryStartButton(fixture)).toBeFalsy();
  });

  function setupFixtureWith(mockSessionService: SessionService): void {
    TestBed.configureTestingModule({
      imports: [QuizSessionComponent],
      providers: [
        { provide: SessionService, useValue: mockSessionService },
        {
          provide: ActivatedRoute,
          useValue: { snapshot: { paramMap: convertToParamMap({ id: SESSION_ID }) } }
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

function queryStartButton(fixture: ComponentFixture<QuizSessionComponent>) {
  return fixture.debugElement.query(By.css('button[type=button]'));
}

function queryNotFoundMessage(fixture: ComponentFixture<QuizSessionComponent>) {
  return fixture.debugElement.query(By.css('p'));
}
