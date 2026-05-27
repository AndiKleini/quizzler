import { Injectable, inject } from "@angular/core";
import { HttpClient } from "@angular/common/http";
import { map, Observable } from "rxjs";
import { QuizAttempt } from "../entities/quizattempt";

const apiBaseUrl = 'http://localhost:8080';

@Injectable({ providedIn: 'root' })
export class QuizAttemptService {
    private http = inject(HttpClient);

    createAttempt(sessionId: string): Observable<QuizAttempt> {
        return this.http.post<QuizAttempt>(`${apiBaseUrl}/session/${sessionId}/attempt`, null).pipe(
            map(r => new QuizAttempt(r.attemptId, r.sessionId, r.questionId))
        );
    }
}
