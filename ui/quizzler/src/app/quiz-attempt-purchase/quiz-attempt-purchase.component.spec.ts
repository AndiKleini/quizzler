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
const PRICE_IN_CENTS = 200;

describe('QuizAttemptPurchaseComponent', () => {
  let fixture: ComponentFixture<QuizAttemptPurchaseComponent>;
  let component: QuizAttemptPurchaseComponent;
  let mockRouter: { navigate: jest.Mock; getCurrentNavigation: jest.Mock };
  let mockQuizAttemptService: { createAttempt: jest.Mock };
  let originalLocation: Location;

  function setup(): void {
    mockRouter = {
      navigate: jest.fn().mockResolvedValue(true),
      getCurrentNavigation: jest.fn().mockReturnValue({ extras: { state: { price: PRICE_IN_CENTS } } })
    };
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
            snapshot: { paramMap: convertToParamMap({ sessionId: SESSION_ID, purchaseId: PURCHASE_ID }) }
          }
        }
      ]
    }).compileComponents();
    fixture = TestBed.createComponent(QuizAttemptPurchaseComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  }

  beforeEach(() => {
    originalLocation = window.location;
  });

  afterEach(() => {
    Object.defineProperty(window, 'location', { configurable: true, value: originalLocation });
  });

  it('displays_the_price_in_euros_from_the_navigation_state', () => {
    setup();

    expect(priceText()).toContain('2.00 €');
  });

  it('next_button_is_deactivated', () => {
    setup();

    expect(nextButton()!.disabled).toBe(true);
  });

  it('onStartPayment_redirects_to_payment_ui_with_purchase_id', () => {
    setup();
    Object.defineProperty(window, 'location', { configurable: true, value: { href: '' } });

    startPaymentButton()!.click();

    expect(window.location.href).toBe(`http://localhost:4201/payment/${PURCHASE_ID}`);
  });

  it('onStart_creates_attempt_with_purchase_id_and_navigates_to_attempt_step', () => {
    setup();

    component.onStart();

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

  function priceText(): string {
    return fixture.debugElement.query(By.css('.price')).nativeElement.textContent;
  }

  function startPaymentButton(): HTMLButtonElement | null {
    const debugEl = fixture.debugElement.queryAll(By.css('button'))[0];
    return debugEl ? debugEl.nativeElement as HTMLButtonElement : null;
  }

  function nextButton(): HTMLButtonElement | null {
    const debugEl = fixture.debugElement.queryAll(By.css('button'))[1];
    return debugEl ? debugEl.nativeElement as HTMLButtonElement : null;
  }
});
