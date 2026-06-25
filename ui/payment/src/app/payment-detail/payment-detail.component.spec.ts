import { ComponentFixture, TestBed } from '@angular/core/testing';
import { ActivatedRoute, Router, convertToParamMap } from '@angular/router';
import { of, throwError, delay } from 'rxjs';
import { By } from '@angular/platform-browser';

import { PaymentDetailComponent, PaymentOutcome } from './payment-detail.component';
import { PaymentService } from '../services/paymentservice';
import { Payment } from '../entities/payment';
import { PaymentConfirmation } from '../entities/payment-confirmation';
import { PaymentCancellation } from '../entities/payment-cancellation';

const PAYMENT_ID = 'aaaaaaaa-bbbb-cccc-dddd-eeeeeeeeeeee';
const CONFIRMATION_ID = '11111111-2222-3333-4444-555555555555';
const CANCELLATION_ID = '99999999-8888-7777-6666-555555555555';
const CREATED_AT = '2026-05-30T10:00:00Z';
const PRICE = 1999;
const REDIRECT_URL = 'http://localhost:4200/quiz-session/s/quiz-attempt-purchase-confirmed/';

describe('PaymentDetailComponent', () => {
  let fixture: ComponentFixture<PaymentDetailComponent>;
  let component: PaymentDetailComponent;
  let mockRouter: { navigate: jest.Mock };
  let mockPaymentService: { getPayment: jest.Mock; confirmPayment: jest.Mock; cancelPayment: jest.Mock };
  let redirectSpy: jest.SpyInstance;

  function setup(): void {
    mockRouter = { navigate: jest.fn().mockResolvedValue(true) };
    mockPaymentService = {
      getPayment: jest.fn().mockReturnValue(of(new Payment(PRICE, REDIRECT_URL))),
      confirmPayment: jest.fn().mockReturnValue(of(new PaymentConfirmation(CONFIRMATION_ID, PAYMENT_ID, CREATED_AT))),
      cancelPayment: jest.fn().mockReturnValue(of(new PaymentCancellation(CANCELLATION_ID, PAYMENT_ID, CREATED_AT)))
    };
    TestBed.configureTestingModule({
      imports: [PaymentDetailComponent],
      providers: [
        { provide: PaymentService, useValue: mockPaymentService },
        { provide: Router, useValue: mockRouter },
        {
          provide: ActivatedRoute,
          useValue: { snapshot: { paramMap: convertToParamMap({ paymentId: PAYMENT_ID }) } }
        }
      ]
    }).compileComponents();
    fixture = TestBed.createComponent(PaymentDetailComponent);
    component = fixture.componentInstance;
    // Stub the full-page redirect so it does not trigger an actual navigation in jsdom.
    redirectSpy = jest.spyOn(component as unknown as { redirect: (url: string) => void }, 'redirect')
      .mockImplementation(() => {});
    fixture.detectChanges();
  }

  it('render_when_route_param_present_then_shows_payment_id_and_action_buttons', () => {
    setup();

    expect(component.paymentId).toEqual(PAYMENT_ID);
    expect(fixture.nativeElement.textContent).toContain(PAYMENT_ID);
    expect(queryButton('confirm')).toBeTruthy();
    expect(queryButton('cancel')).toBeTruthy();
  });

  it('ngOnInit_when_payment_loaded_then_displays_amount', () => {
    setup();

    expect(mockPaymentService.getPayment).toHaveBeenCalledWith(PAYMENT_ID);
    expect(component.price()).toBe(PRICE);
    expect(fixture.nativeElement.textContent).toContain(String(PRICE));
  });

  it('ngOnInit_when_load_fails_then_redirects_to_error', () => {
    mockPaymentService = {
      getPayment: jest.fn().mockReturnValue(throwError(() => ({ status: 404 }))),
      confirmPayment: jest.fn(),
      cancelPayment: jest.fn()
    };
    TestBed.configureTestingModule({
      imports: [PaymentDetailComponent],
      providers: [
        { provide: PaymentService, useValue: mockPaymentService },
        { provide: Router, useValue: { navigate: jest.fn().mockResolvedValue(true) } },
        {
          provide: ActivatedRoute,
          useValue: { snapshot: { paramMap: convertToParamMap({ paymentId: PAYMENT_ID }) } }
        }
      ]
    }).compileComponents();
    fixture = TestBed.createComponent(PaymentDetailComponent);
    fixture.detectChanges();

    expect(TestBed.inject(Router).navigate).toHaveBeenCalledWith(['/error']);
    expect(fixture.componentInstance.price()).toBeUndefined();
  });

  it('onConfirm_when_confirmed_then_posts_confirmation_marks_outcome_and_redirects_to_redirect_url', () => {
    setup();

    queryButton('confirm')!.click();
    fixture.detectChanges();

    expect(mockPaymentService.confirmPayment).toHaveBeenCalledWith(PAYMENT_ID);
    expect(component.outcome()).toBe(PaymentOutcome.Confirmed);
    expect(redirectSpy).toHaveBeenCalledWith(REDIRECT_URL);
  });

  it('onConfirm_when_service_throws_then_redirects_to_error', () => {
    setup();
    mockPaymentService.confirmPayment.mockReturnValue(throwError(() => ({ status: 409 })));

    component.onConfirm();

    expect(mockRouter.navigate).toHaveBeenCalledWith(['/error']);
    expect(component.outcome()).toBe(PaymentOutcome.Pending);
  });

  it('onCancel_when_cancelled_then_posts_cancellation_marks_outcome_and_hides_buttons', () => {
    setup();

    queryButton('cancel')!.click();
    fixture.detectChanges();

    expect(mockPaymentService.cancelPayment).toHaveBeenCalledWith(PAYMENT_ID);
    expect(component.outcome()).toBe(PaymentOutcome.Cancelled);
    expect(queryButton('confirm')).toBeFalsy();
    expect(queryButton('cancel')).toBeFalsy();
  });

  it('onCancel_when_service_throws_then_redirects_to_error', () => {
    setup();
    mockPaymentService.cancelPayment.mockReturnValue(throwError(() => ({ status: 409 })));

    component.onCancel();

    expect(mockRouter.navigate).toHaveBeenCalledWith(['/error']);
    expect(component.outcome()).toBe(PaymentOutcome.Pending);
  });

  it('onConfirm_when_confirming_then_displays_loading_spinner', () => {
    setup();
    // Use a delayed observable to simulate async call so isLoading remains true
    mockPaymentService.confirmPayment.mockReturnValue(
      of(new PaymentConfirmation(CONFIRMATION_ID, PAYMENT_ID, CREATED_AT)).pipe(delay(100))
    );

    component.onConfirm();
    fixture.detectChanges();

    expect(component.isLoading()).toBe(true);
    const spinner = fixture.debugElement.query(By.css('payment-loading-spinner'));
    expect(spinner).toBeTruthy();
    expect(queryButton('confirm')).toBeFalsy();
    expect(queryButton('cancel')).toBeFalsy();
  });

  function queryButton(text: string): HTMLButtonElement | null {
    const debugEl = fixture.debugElement.queryAll(By.css('button'))
      .find(el => (el.nativeElement.textContent as string).toLowerCase().includes(text));
    return debugEl ? debugEl.nativeElement as HTMLButtonElement : null;
  }
});
