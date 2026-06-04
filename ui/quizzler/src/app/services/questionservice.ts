import { Injectable, inject } from "@angular/core";
import { HttpClient } from "@angular/common/http";
import { Observable } from "rxjs";
import { SinglePickQuestion } from "../entities/singlepickquestion";
import { SinglePickResult } from "../entities/singlepickresult";
import { environment } from "../../environments/environment";

const apiBaseUrl = environment.apiBaseUrl;

@Injectable({  providedIn: 'root'})
export class QuestionService {
    private http = inject(HttpClient);

    getSinglePickQuestionById(id: number): Observable<SinglePickQuestion> {
      return this.http.get<SinglePickQuestion>(`${apiBaseUrl}/question/${id}`);
    }
    // method is currently stubbed out only
    // eslint-disable-next-line
    evaluate(question: SinglePickQuestion, selectedOptionId: number) : SinglePickResult {
      return new SinglePickResult(2);
    }
}
