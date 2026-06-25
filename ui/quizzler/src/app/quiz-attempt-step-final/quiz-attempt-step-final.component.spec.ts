import { ComponentFixture, TestBed } from '@angular/core/testing';

import { QuizAttemptStepFinalComponent } from './quiz-attempt-step-final.component';
import { ActivatedRoute } from '@angular/router';

const SESSIONID_KEY = 'test-session-id';
const ATTEMPTID_KEY = 'test-attempt-id';
describe('QuizAttemptStepFinalComponent', () => {
  let component: QuizAttemptStepFinalComponent;
  let fixture: ComponentFixture<QuizAttemptStepFinalComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [QuizAttemptStepFinalComponent],
      providers: [
        {
          provide: ActivatedRoute,
          useValue: {
            snapshot: {
              paramMap: {
                get: (key: string) => {
                  if (key === 'sessionId') return SESSIONID_KEY;
                  if (key === 'attemptId') return ATTEMPTID_KEY;
                  return null;
                }
              }
            }
          }
        }
      ]
    })
    .compileComponents();

    fixture = TestBed.createComponent(QuizAttemptStepFinalComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
    expect(component.sessionId).toBe(SESSIONID_KEY);
    expect(component.attemptId).toBe(ATTEMPTID_KEY);
    var text = fixture.nativeElement.querySelector('p:nth-child(3)').textContent.trim();
    expect(text).toContain(SESSIONID_KEY);
    expect(text).toContain(ATTEMPTID_KEY);
  });
});
