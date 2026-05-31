import { TestBed } from '@angular/core/testing';
import { provideHttpClient } from '@angular/common/http';
import { HttpTestingController, provideHttpClientTesting } from '@angular/common/http/testing';

import { QuizAttemptPurchaseService } from './quiz-attempt-purchaseservice';
import { QuizAttemptPurchase } from '../entities/quizattemptpurchase';
import { QuizAttemptPurchaseConfirmation } from '../entities/quizattemptpurchaseconfirmation';

const SESSION_ID = '33d24a21-3f56-42c6-a959-6567ca56139e';
const PURCHASE_ID = '99999999-8888-7777-6666-555555555555';
const PAYMENT_ID = '12121212-3434-5656-7878-909090909090';
const CONFIRMATION_ID = '11111111-2222-3333-4444-555555555555';
const CREATED_AT = '2026-05-30T10:00:00Z';
const PRICE_IN_CENTS = 200;
const PURCHASE_URL = `http://localhost:8080/session/${SESSION_ID}/quiz-attempt-purchase`;
const PAYMENT_URL = `${PURCHASE_URL}/${PURCHASE_ID}/payment`;
const CONFIRMATION_URL = `${PURCHASE_URL}/${PURCHASE_ID}/confirmation`;

describe('QuizAttemptPurchaseService', () => {
  let service: QuizAttemptPurchaseService;
  let httpMock: HttpTestingController;

  beforeEach(() => {
    TestBed.configureTestingModule({
      providers: [
        QuizAttemptPurchaseService,
        provideHttpClient(),
        provideHttpClientTesting()
      ]
    });
    service = TestBed.inject(QuizAttemptPurchaseService);
    httpMock = TestBed.inject(HttpTestingController);
  });

  afterEach(() => httpMock.verify());

  it('createPurchase_when_response_is_json_payload_maps_to_quiz_attempt_purchase_instance', () => {
    const expected = new QuizAttemptPurchase(PURCHASE_ID, SESSION_ID, PRICE_IN_CENTS);
    let actual: QuizAttemptPurchase | undefined;

    service.createPurchase(SESSION_ID).subscribe(p => actual = p);
    const req = httpMock.expectOne(PURCHASE_URL);
    expect(req.request.method).toBe('POST');
    req.flush({ purchaseId: PURCHASE_ID, sessionId: SESSION_ID, price: PRICE_IN_CENTS });

    expect(actual).toBeInstanceOf(QuizAttemptPurchase);
    expect(actual).toEqual(expected);
  });

  it('initiatePayment_when_response_is_json_payload_maps_to_payment_id', () => {
    let actual: string | undefined;

    service.initiatePayment(SESSION_ID, PURCHASE_ID).subscribe(id => actual = id);
    const req = httpMock.expectOne(PAYMENT_URL);
    expect(req.request.method).toBe('POST');
    req.flush({ paymentId: PAYMENT_ID });

    expect(actual).toBe(PAYMENT_ID);
  });

  it('getConfirmation_when_response_is_json_payload_maps_to_confirmation_instance', () => {
    const expected = new QuizAttemptPurchaseConfirmation(CONFIRMATION_ID, PURCHASE_ID, CREATED_AT);
    let actual: QuizAttemptPurchaseConfirmation | undefined;

    service.getConfirmation(SESSION_ID, PURCHASE_ID).subscribe(c => actual = c);
    const req = httpMock.expectOne(CONFIRMATION_URL);
    expect(req.request.method).toBe('GET');
    req.flush({ confirmationId: CONFIRMATION_ID, purchaseId: PURCHASE_ID, createdAt: CREATED_AT });

    expect(actual).toBeInstanceOf(QuizAttemptPurchaseConfirmation);
    expect(actual).toEqual(expected);
  });

  it('getConfirmation_when_server_responds_404_propagates_error', () => {
    let caught: { status: number } | undefined;

    service.getConfirmation(SESSION_ID, PURCHASE_ID).subscribe({
      next: () => fail('expected error'),
      error: err => caught = err
    });
    httpMock.expectOne(CONFIRMATION_URL)
      .flush('Not Found', { status: 404, statusText: 'Not Found' });

    expect(caught?.status).toBe(404);
  });

  it('createPurchase_when_server_responds_404_propagates_error', () => {
    let caught: { status: number } | undefined;

    service.createPurchase(SESSION_ID).subscribe({
      next: () => fail('expected error'),
      error: err => caught = err
    });
    httpMock.expectOne(PURCHASE_URL)
      .flush('Not Found', { status: 404, statusText: 'Not Found' });

    expect(caught?.status).toBe(404);
  });
});
