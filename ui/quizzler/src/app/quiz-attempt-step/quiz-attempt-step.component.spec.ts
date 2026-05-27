import { ComponentFixture, TestBed } from '@angular/core/testing';
import { ActivatedRoute, convertToParamMap } from '@angular/router';

import { QuizAttemptStepComponent } from './quiz-attempt-step.component';

const SESSION_ID = '33d24a21-3f56-42c6-a959-6567ca56139e';
const QUESTION_ID = '42';

describe('QuizAttemptStepComponent', () => {
  let fixture: ComponentFixture<QuizAttemptStepComponent>;

  it('render_when_route_params_present_then_shows_session_and_question', () => {
    TestBed.configureTestingModule({
      imports: [QuizAttemptStepComponent],
      providers: [
        {
          provide: ActivatedRoute,
          useValue: { snapshot: { paramMap: convertToParamMap({ sessionId: SESSION_ID, questionId: QUESTION_ID }) } }
        }
      ]
    }).compileComponents();
    fixture = TestBed.createComponent(QuizAttemptStepComponent);
    fixture.detectChanges();

    const text: string = fixture.nativeElement.textContent;
    expect(text).toContain(SESSION_ID);
    expect(text).toContain(QUESTION_ID);
  });
});
