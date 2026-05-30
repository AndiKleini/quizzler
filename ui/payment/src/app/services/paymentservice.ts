import { Injectable, inject } from "@angular/core";
import { HttpClient } from "@angular/common/http";
import { map, Observable } from "rxjs";
import { PaymentConfirmation } from "../entities/payment-confirmation";
import { PaymentCancellation } from "../entities/payment-cancellation";

const apiBaseUrl = 'http://localhost:8081';

@Injectable({ providedIn: 'root' })
export class PaymentService {
    private http = inject(HttpClient);

    confirmPayment(paymentId: string): Observable<PaymentConfirmation> {
        return this.http.post<PaymentConfirmation>(`${apiBaseUrl}/payment/${paymentId}/confirmation`, null).pipe(
            map(r => new PaymentConfirmation(r.confirmationId, r.paymentId, r.createdAt))
        );
    }

    cancelPayment(paymentId: string): Observable<PaymentCancellation> {
        return this.http.post<PaymentCancellation>(`${apiBaseUrl}/payment/${paymentId}/cancellation`, null).pipe(
            map(r => new PaymentCancellation(r.cancellationId, r.paymentId, r.createdAt))
        );
    }
}
