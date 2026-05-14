import { SingePickOption } from './singlepickoption';

export class SinglePickQuestion {
    constructor(
        public id: number,
        public title: string,
        public text: string,
        public options: SingePickOption[]
    ) { };
}