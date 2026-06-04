import { Injectable, inject } from "@angular/core";
import { HttpClient } from "@angular/common/http";
import { map, Observable } from "rxjs";
import { QuizAttemptPurchase } from "../entities/quizattemptpurchase";
import { QuizAttemptPurchaseConfirmation } from "../entities/quizattemptpurchaseconfirmation";
import { environment } from "../../environments/environment";

const apiBaseUrl = environment.apiBaseUrl;

@Injectable({ providedIn: 'root' })
export class QuizAttemptPurchaseService {
    private http = inject(HttpClient);

    createPurchase(sessionId: string): Observable<QuizAttemptPurchase> {
        return this.http.post<QuizAttemptPurchase>(`${apiBaseUrl}/session/${sessionId}/quiz-attempt-purchase`, null).pipe(
            map(r => new QuizAttemptPurchase(r.purchaseId, r.sessionId, r.price))
        );
    }

    getConfirmation(sessionId: string, purchaseId: string): Observable<QuizAttemptPurchaseConfirmation> {
        return this.http.get<QuizAttemptPurchaseConfirmation>(
            `${apiBaseUrl}/session/${sessionId}/quiz-attempt-purchase/${purchaseId}/confirmation`).pipe(
            map(r => new QuizAttemptPurchaseConfirmation(r.confirmationId, r.purchaseId, r.createdAt))
        );
    }

    initiatePayment(sessionId: string, purchaseId: string): Observable<string> {
        return this.http.post<{ paymentId: string }>(
            `${apiBaseUrl}/session/${sessionId}/quiz-attempt-purchase/${purchaseId}/payment`, null).pipe(
            map(r => r.paymentId)
        );
    }
}
