import { ComponentFixture, TestBed } from '@angular/core/testing';
import { ActivatedRoute, Router, convertToParamMap } from '@angular/router';
import { of, throwError } from 'rxjs';
import { By } from '@angular/platform-browser';

import { PaymentDetailComponent, PaymentOutcome } from './payment-detail.component';
import { PaymentService } from '../services/paymentservice';
import { PaymentConfirmation } from '../entities/payment-confirmation';
import { PaymentCancellation } from '../entities/payment-cancellation';

const PAYMENT_ID = 'aaaaaaaa-bbbb-cccc-dddd-eeeeeeeeeeee';
const CONFIRMATION_ID = '11111111-2222-3333-4444-555555555555';
const CANCELLATION_ID = '99999999-8888-7777-6666-555555555555';
const CREATED_AT = '2026-05-30T10:00:00Z';

describe('PaymentDetailComponent', () => {
  let fixture: ComponentFixture<PaymentDetailComponent>;
  let component: PaymentDetailComponent;
  let mockRouter: { navigate: jest.Mock };
  let mockPaymentService: { confirmPayment: jest.Mock; cancelPayment: jest.Mock };

  function setup(): void {
    mockRouter = { navigate: jest.fn().mockResolvedValue(true) };
    mockPaymentService = {
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
    fixture.detectChanges();
  }

  it('render_when_route_param_present_then_shows_payment_id_and_action_buttons', () => {
    setup();

    expect(component.paymentId).toEqual(PAYMENT_ID);
    expect(fixture.nativeElement.textContent).toContain(PAYMENT_ID);
    expect(queryButton('confirm')).toBeTruthy();
    expect(queryButton('cancel')).toBeTruthy();
  });

  it('onConfirm_when_confirmed_then_posts_confirmation_marks_outcome_and_hides_buttons', () => {
    setup();

    queryButton('confirm')!.click();
    fixture.detectChanges();

    expect(mockPaymentService.confirmPayment).toHaveBeenCalledWith(PAYMENT_ID);
    expect(component.outcome()).toBe(PaymentOutcome.Confirmed);
    expect(queryButton('confirm')).toBeFalsy();
    expect(queryButton('cancel')).toBeFalsy();
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

  function queryButton(text: string): HTMLButtonElement | null {
    const debugEl = fixture.debugElement.queryAll(By.css('button'))
      .find(el => (el.nativeElement.textContent as string).toLowerCase().includes(text));
    return debugEl ? debugEl.nativeElement as HTMLButtonElement : null;
  }
});
