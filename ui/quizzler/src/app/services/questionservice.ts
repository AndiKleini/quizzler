import { Injectable } from "@angular/core";
import { SinglePickQuestion } from "../entities/singlepickquestion";
import { SingePickOption } from "../entities/singlepickoption";

@Injectable({  providedIn: 'root'})
export class QuestionService {
    getSinglePickQuestionById(id: number) : SinglePickQuestion {
        return  new SinglePickQuestion(
            "Question ES 1", 
            "This is the text of a single pick question !",
            [ 
              new SingePickOption(1, 'Option 1'), 
              new SingePickOption(2, 'Option 2'),
              new SingePickOption(3, 'Option 3'),
              new SingePickOption(4, 'Option 4')
            ]);
    }
}