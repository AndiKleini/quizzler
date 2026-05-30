import { TestBed } from '@angular/core/testing';
import { provideHttpClient } from '@angular/common/http';
import { HttpTestingController, provideHttpClientTesting } from '@angular/common/http/testing';

import { QuizAttemptPurchaseService } from './quiz-attempt-purchaseservice';
import { QuizAttemptPurchase } from '../entities/quizattemptpurchase';

const SESSION_ID = '33d24a21-3f56-42c6-a959-6567ca56139e';
const PURCHASE_ID = '99999999-8888-7777-6666-555555555555';
const PURCHASE_URL = `http://localhost:8080/session/${SESSION_ID}/quiz-attempt-purchase`;

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
    const expected = new QuizAttemptPurchase(PURCHASE_ID, SESSION_ID);
    let actual: QuizAttemptPurchase | undefined;

    service.createPurchase(SESSION_ID).subscribe(p => actual = p);
    const req = httpMock.expectOne(PURCHASE_URL);
    expect(req.request.method).toBe('POST');
    req.flush({ purchaseId: PURCHASE_ID, sessionId: SESSION_ID });

    expect(actual).toBeInstanceOf(QuizAttemptPurchase);
    expect(actual).toEqual(expected);
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
