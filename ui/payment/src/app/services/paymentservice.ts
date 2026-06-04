import { Injectable, inject } from "@angular/core";
import { HttpClient } from "@angular/common/http";
import { map, Observable } from "rxjs";
import { Payment } from "../entities/payment";
import { PaymentConfirmation } from "../entities/payment-confirmation";
import { PaymentCancellation } from "../entities/payment-cancellation";
import { environment } from "../../environments/environment";

const apiBaseUrl = environment.apiBaseUrl;

@Injectable({ providedIn: 'root' })
export class PaymentService {
    private http = inject(HttpClient);

    getPayment(paymentId: string): Observable<Payment> {
        return this.http.get<Payment>(`${apiBaseUrl}/payment/${paymentId}`).pipe(
            map(r => new Payment(r.price, r.redirectUrl))
        );
    }

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
