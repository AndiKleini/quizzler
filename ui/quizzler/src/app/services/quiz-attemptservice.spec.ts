import { TestBed } from '@angular/core/testing';
import { provideHttpClient } from '@angular/common/http';
import { HttpTestingController, provideHttpClientTesting } from '@angular/common/http/testing';

import { QuizAttemptService } from './quiz-attemptservice';
import { QuizAttempt } from '../entities/quizattempt';
import { Answer } from '../entities/answer';

const SESSION_ID = '33d24a21-3f56-42c6-a959-6567ca56139e';
const ATTEMPT_ID = 'aaaaaaaa-bbbb-cccc-dddd-eeeeeeeeeeee';
const QUESTION_ID = 42;
const SELECTED_OPTION_ID = 3;
const ANSWER_ID = 7;
const CORRECT_OPTION_ID = 2;
const SUBMITTED_AT = '2026-05-28T10:00:00Z';
const ATTEMPT_URL = `http://localhost:8080/session/${SESSION_ID}/attempt`;
const ATTEMPT_BY_ID_URL = `${ATTEMPT_URL}/${ATTEMPT_ID}`;
const ANSWER_URL = `${ATTEMPT_BY_ID_URL}/answer`;

describe('QuizAttemptService', () => {
  let service: QuizAttemptService;
  let httpMock: HttpTestingController;

  beforeEach(() => {
    TestBed.configureTestingModule({
      providers: [
        QuizAttemptService,
        provideHttpClient(),
        provideHttpClientTesting()
      ]
    });
    service = TestBed.inject(QuizAttemptService);
    httpMock = TestBed.inject(HttpTestingController);
  });

  afterEach(() => httpMock.verify());

  it('createAttempt_when_response_is_json_payload_maps_to_quiz_attempt_instance', () => {
    const expected = new QuizAttempt(ATTEMPT_ID, SESSION_ID, QUESTION_ID);
    let actual: QuizAttempt | undefined;

    service.createAttempt(SESSION_ID).subscribe(a => actual = a);
    const req = httpMock.expectOne(ATTEMPT_URL);
    expect(req.request.method).toBe('POST');
    req.flush({ attemptId: ATTEMPT_ID, sessionId: SESSION_ID, questionId: QUESTION_ID });

    expect(actual).toBeInstanceOf(QuizAttempt);
    expect(actual).toEqual(expected);
  });

  it('createAttempt_when_server_responds_404_propagates_error', () => {
    let caught: { status: number } | undefined;

    service.createAttempt(SESSION_ID).subscribe({
      next: () => fail('expected error'),
      error: err => caught = err
    });
    httpMock.expectOne(ATTEMPT_URL)
      .flush('Not Found', { status: 404, statusText: 'Not Found' });

    expect(caught?.status).toBe(404);
  });

  it('getAttempt_when_response_is_json_payload_maps_to_quiz_attempt_instance', () => {
    const expected = new QuizAttempt(ATTEMPT_ID, SESSION_ID, QUESTION_ID);
    let actual: QuizAttempt | undefined;

    service.getAttempt(SESSION_ID, ATTEMPT_ID).subscribe(a => actual = a);
    const req = httpMock.expectOne(ATTEMPT_BY_ID_URL);
    expect(req.request.method).toBe('GET');
    req.flush({ attemptId: ATTEMPT_ID, sessionId: SESSION_ID, questionId: QUESTION_ID });

    expect(actual).toBeInstanceOf(QuizAttempt);
    expect(actual).toEqual(expected);
  });

  it('getAttempt_when_server_responds_404_propagates_error', () => {
    let caught: { status: number } | undefined;

    service.getAttempt(SESSION_ID, ATTEMPT_ID).subscribe({
      next: () => fail('expected error'),
      error: err => caught = err
    });
    httpMock.expectOne(ATTEMPT_BY_ID_URL)
      .flush('Not Found', { status: 404, statusText: 'Not Found' });

    expect(caught?.status).toBe(404);
  });

  it('submitAnswer_when_response_is_json_payload_posts_submission_and_maps_to_answer_instance', () => {
    const expected = new Answer(ANSWER_ID, ATTEMPT_ID, QUESTION_ID, SELECTED_OPTION_ID, CORRECT_OPTION_ID, SUBMITTED_AT);
    let actual: Answer | undefined;

    service.submitAnswer(SESSION_ID, ATTEMPT_ID, QUESTION_ID, SELECTED_OPTION_ID).subscribe(a => actual = a);
    const req = httpMock.expectOne(ANSWER_URL);
    expect(req.request.method).toBe('POST');
    expect(req.request.body).toEqual({ questionId: QUESTION_ID, selectedOptionId: SELECTED_OPTION_ID });
    req.flush({
      id: ANSWER_ID,
      attemptId: ATTEMPT_ID,
      questionId: QUESTION_ID,
      selectedOptionId: SELECTED_OPTION_ID,
      correctOptionId: CORRECT_OPTION_ID,
      submittedAt: SUBMITTED_AT
    });

    expect(actual).toBeInstanceOf(Answer);
    expect(actual).toEqual(expected);
  });

  it('submitAnswer_when_server_responds_404_propagates_error', () => {
    let caught: { status: number } | undefined;

    service.submitAnswer(SESSION_ID, ATTEMPT_ID, QUESTION_ID, SELECTED_OPTION_ID).subscribe({
      next: () => fail('expected error'),
      error: err => caught = err
    });
    httpMock.expectOne(ANSWER_URL)
      .flush('Not Found', { status: 404, statusText: 'Not Found' });

    expect(caught?.status).toBe(404);
  });
});
