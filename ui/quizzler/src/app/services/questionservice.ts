import { Injectable } from "@angular/core";
import { SinglePickQuestion } from "../entities/singlepickquestion";
import { SingePickOption } from "../entities/singlepickoption";

@Injectable({  providedIn: 'root'})
export class QuestionService {
    getSinglePickQuestionById(id: number) : SinglePickQuestion {
        return  new SinglePickQuestion(
            "Question ES 1", 
            "Which of the following qualities can most likely be improved by using a layered architecture?",
            [ 
              new SingePickOption(1, 'Runtime efficiency (performance).'), 
              new SingePickOption(2, 'Flexibility in modifying or changing the system.'),
              new SingePickOption(3, 'Flexibility at runtime (configurability).'),
              new SingePickOption(4, 'Non-repudiability.')
            ]);
    }
}