import { Injectable } from '@angular/core';

import { Question } from '../entities/question';
import { Observable, of } from 'rxjs';

@Injectable({
  providedIn: 'root'
})
export class QuestionService {
  public getQuestion(questionId: number): Observable<Question> {
     return of(new Question(questionId, 
      ` This is comming from the service. This is the quetsion that was asked. And the candidate has to checkout the correct
     answers. The text can be a bit longer as we are here to learn and learn and learn 
     and learn. This is the quetsion that was asked. And the candidate has to checkout the correct
     answers. The text can be a bit longer as we are here to learn and learn and learn 
     and learn. This is the quetsion that was asked. And the candidate has to checkout the correct
     answers. The text can be a bit longer as we are here to learn and learn and learn 
     and learn. This is the quetsion that was asked. And the candidate has to checkout the correct
     answers. The text can be a bit longer as we are here to learn and learn and learn 
     and learn.`));
  }
}
