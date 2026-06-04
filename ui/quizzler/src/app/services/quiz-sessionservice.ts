import { Injectable, inject } from "@angular/core";
import { HttpClient } from "@angular/common/http";
import { catchError, map, Observable, of, throwError } from "rxjs";
import { QuizSession } from "../entities/quizsession";
import { environment } from "../../environments/environment";

const apiBaseUrl = environment.apiBaseUrl;

@Injectable({ providedIn: 'root' })
export class SessionService {
    private http = inject(HttpClient);

    getSessionById(id: string): Observable<QuizSession> {
      return this.http.get<QuizSession>(`${apiBaseUrl}/session/${id}`).pipe(
        map(r => new QuizSession(r.publicId)),
        catchError(err => {
          if (err.status === 404) {
            return of(QuizSession.getDefaultQuizSession());
          }
          return throwError(() => err);
        })
      );
    }
}
