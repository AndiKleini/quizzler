import { TestBed } from '@angular/core/testing';
import { provideHttpClient } from '@angular/common/http';
import { HttpTestingController, provideHttpClientTesting } from '@angular/common/http/testing';

import { PaymentService } from './paymentservice';
import { PaymentConfirmation } from '../entities/payment-confirmation';
import { PaymentCancellation } from '../entities/payment-cancellation';

const PAYMENT_ID = 'aaaaaaaa-bbbb-cccc-dddd-eeeeeeeeeeee';
const CONFIRMATION_ID = '11111111-2222-3333-4444-555555555555';
const CANCELLATION_ID = '99999999-8888-7777-6666-555555555555';
const CREATED_AT = '2026-05-30T10:00:00Z';
const CONFIRMATION_URL = `http://localhost:8081/payment/${PAYMENT_ID}/confirmation`;
const CANCELLATION_URL = `http://localhost:8081/payment/${PAYMENT_ID}/cancellation`;

describe('PaymentService', () => {
  let service: PaymentService;
  let httpMock: HttpTestingController;

  beforeEach(() => {
    TestBed.configureTestingModule({
      providers: [
        PaymentService,
        provideHttpClient(),
        provideHttpClientTesting()
      ]
    });
    service = TestBed.inject(PaymentService);
    httpMock = TestBed.inject(HttpTestingController);
  });

  afterEach(() => httpMock.verify());

  it('confirmPayment_when_response_is_json_payload_maps_to_payment_confirmation_instance', () => {
    const expected = new PaymentConfirmation(CONFIRMATION_ID, PAYMENT_ID, CREATED_AT);
    let actual: PaymentConfirmation | undefined;

    service.confirmPayment(PAYMENT_ID).subscribe(c => actual = c);
    const req = httpMock.expectOne(CONFIRMATION_URL);
    expect(req.request.method).toBe('POST');
    req.flush({ confirmationId: CONFIRMATION_ID, paymentId: PAYMENT_ID, createdAt: CREATED_AT });

    expect(actual).toBeInstanceOf(PaymentConfirmation);
    expect(actual).toEqual(expected);
  });

  it('confirmPayment_when_server_responds_409_propagates_error', () => {
    let caught: { status: number } | undefined;

    service.confirmPayment(PAYMENT_ID).subscribe({
      next: () => fail('expected error'),
      error: err => caught = err
    });
    httpMock.expectOne(CONFIRMATION_URL)
      .flush('Conflict', { status: 409, statusText: 'Conflict' });

    expect(caught?.status).toBe(409);
  });

  it('cancelPayment_when_response_is_json_payload_maps_to_payment_cancellation_instance', () => {
    const expected = new PaymentCancellation(CANCELLATION_ID, PAYMENT_ID, CREATED_AT);
    let actual: PaymentCancellation | undefined;

    service.cancelPayment(PAYMENT_ID).subscribe(c => actual = c);
    const req = httpMock.expectOne(CANCELLATION_URL);
    expect(req.request.method).toBe('POST');
    req.flush({ cancellationId: CANCELLATION_ID, paymentId: PAYMENT_ID, createdAt: CREATED_AT });

    expect(actual).toBeInstanceOf(PaymentCancellation);
    expect(actual).toEqual(expected);
  });

  it('cancelPayment_when_server_responds_409_propagates_error', () => {
    let caught: { status: number } | undefined;

    service.cancelPayment(PAYMENT_ID).subscribe({
      next: () => fail('expected error'),
      error: err => caught = err
    });
    httpMock.expectOne(CANCELLATION_URL)
      .flush('Conflict', { status: 409, statusText: 'Conflict' });

    expect(caught?.status).toBe(409);
  });
});
