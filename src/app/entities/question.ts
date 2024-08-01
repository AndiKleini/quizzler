import  { Option } from "./option";

export class Question {
    constructor(
        public id: number, 
        public text: string,
        public options: Option[]) {}
}