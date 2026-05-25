import { Injectable, inject } from "@angular/core";
import { HttpClient } from "@angular/common/http";
import { catchError, map, Observable, of, throwError } from "rxjs";
import { QuizSession } from "../entities/quizsession";

const apiBaseUrl = 'http://localhost:8080';

@Injectable({ providedIn: 'root' })
export class SessionService {
    private http = inject(HttpClient);

    getSessionById(id: string): Observable<QuizSession> {
      return this.http.get<QuizSession>(`${apiBaseUrl}/session/${id}`).pipe(
        map(r => new QuizSession(r.publicId, r.currentQuestion, r.nextQuestion, r.previousQuestion)),
        catchError(err => {
          if (err.status === 404) {
            return of(QuizSession.getDefaultQuizSession());
          }
          return throwError(() => err);
        })
      );
    }
}
