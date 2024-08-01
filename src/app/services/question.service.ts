import { Injectable } from '@angular/core';

import { Question } from '../entities/question';
import { Option } from '../entities/option';
import { Observable, of } from 'rxjs';

@Injectable({
  providedIn: 'root'
})
export class QuestionService {
  public getQuestion(questionId: number): Observable<Question> {
    if (questionId == 2) {
      return of(new Question(
        questionId, 
        ` This is the question with the id 2. This is the quetsion that was asked. And the candidate has to checkout the correct
       answers. The text can be a bit longer as we are here to learn and learn and learn 
       and learn. This is the quetsion that was asked. And the candidate has to checkout the correct
       answers. The text can be a bit longer as we are here to learn and learn and learn 
       and learn. This is the quetsion that was asked. And the candidate has to checkout the correct
       answers. The text can be a bit longer as we are here to learn and learn and learn 
       and learn. This is the quetsion that was asked. And the candidate has to checkout the correct
       answers. The text can be a bit longer as we are here to learn and learn and learn 
       and learn.`, 
       [ 
          {text: "Is this true or false 1 ?", selected: false },
          {text: "Is this true or false 2 ?", selected: false },
          {text: "Is this true or false 3 ?", selected: false },
        ] ));
    }
     return of(new Question(questionId, 
      ` This is comming from the service. This is the quetsion that was asked. And the candidate has to checkout the correct
     answers. The text can be a bit longer as we are here to learn and learn and learn 
     and learn. This is the quetsion that was asked. And the candidate has to checkout the correct
     answers. The text can be a bit longer as we are here to learn and learn and learn 
     and learn. This is the quetsion that was asked. And the candidate has to checkout the correct
     answers. The text can be a bit longer as we are here to learn and learn and learn 
     and learn. This is the quetsion that was asked. And the candidate has to checkout the correct
     answers. The text can be a bit longer as we are here to learn and learn and learn 
     and learn.`, [ {text: "Can we apply this", selected: false } ]));
  }
}
