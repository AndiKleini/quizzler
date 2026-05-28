import { TestBed } from '@angular/core/testing';
import { provideHttpClient } from '@angular/common/http';
import { HttpTestingController, provideHttpClientTesting } from '@angular/common/http/testing';

import { SessionService } from './quiz-sessionservice';
import { QuizSession } from '../entities/quizsession';

const SESSION_ID = '33d24a21-3f56-42c6-a959-6567ca56139e';
const SESSION_URL = `http://localhost:8080/session/${SESSION_ID}`;

describe('SessionService', () => {
  let service: SessionService;
  let httpMock: HttpTestingController;

  beforeEach(() => {
    TestBed.configureTestingModule({
      providers: [
        SessionService,
        provideHttpClient(),
        provideHttpClientTesting()
      ]
    });
    service = TestBed.inject(SessionService);
    httpMock = TestBed.inject(HttpTestingController);
  });

  afterEach(() => httpMock.verify());

  it('getSessionById_when_response_is_json_payload_maps_to_quiz_session_instance', () => {
    const expected = new QuizSession(SESSION_ID);
    let actual: QuizSession | undefined;

    service.getSessionById(SESSION_ID).subscribe(s => actual = s);
    httpMock.expectOne(SESSION_URL).flush({ publicId: SESSION_ID });

    expect(actual).toBeInstanceOf(QuizSession);
    expect(actual).toEqual(expected);
    expect(actual!.isDefault()).toBe(false);
  });

  it('getSessionById_when_server_responds_404_returns_default_session', () => {
    let actual: QuizSession | undefined;

    service.getSessionById(SESSION_ID).subscribe(s => actual = s);
    httpMock.expectOne(SESSION_URL).flush('Not Found', { status: 404, statusText: 'Not Found' });

    expect(actual).toEqual(QuizSession.getDefaultQuizSession());
    expect(actual!.isDefault()).toBe(true);
  });

  it('getSessionById_when_server_responds_500_propagates_error', () => {
    let caught: { status: number } | undefined;

    service.getSessionById(SESSION_ID).subscribe({
      next: () => fail('expected error'),
      error: err => caught = err
    });
    httpMock.expectOne(SESSION_URL)
      .flush('Server Error', { status: 500, statusText: 'Internal Server Error' });

    expect(caught?.status).toBe(500);
  });
});
