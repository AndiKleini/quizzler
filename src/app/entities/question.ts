import  { Option } from "./option";

export class Question {
    constructor(
        public id: number, 
        public text: string,
        public options: Option[]) {

        }

    toString(): string {
        console.log('in tostring of Question');
        return `${this.text}\n${this.options.map((o,i,opts) => opts[i].toString()).join()}`;
    }
}