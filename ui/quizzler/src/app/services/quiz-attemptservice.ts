import { Injectable, inject } from "@angular/core";
import { HttpClient } from "@angular/common/http";
import { map, Observable } from "rxjs";
import { QuizAttempt } from "../entities/quizattempt";
import { Answer } from "../entities/answer";

const apiBaseUrl = 'http://localhost:8080';

@Injectable({ providedIn: 'root' })
export class QuizAttemptService {
    private http = inject(HttpClient);

    createAttempt(sessionId: string): Observable<QuizAttempt> {
        return this.http.post<QuizAttempt>(`${apiBaseUrl}/session/${sessionId}/attempt`, null).pipe(
            map(r => new QuizAttempt(r.attemptId, r.sessionId, r.questionId))
        );
    }

    submitAnswer(sessionId: string, attemptId: string, questionId: number, selectedOptionId: number): Observable<Answer> {
        return this.http.post<Answer>(
            `${apiBaseUrl}/session/${sessionId}/attempt/${attemptId}/answer`,
            { questionId, selectedOptionId }
        ).pipe(
            map(r => new Answer(r.id, r.attemptId, r.questionId, r.selectedOptionId, r.submittedAt))
        );
    }
}
