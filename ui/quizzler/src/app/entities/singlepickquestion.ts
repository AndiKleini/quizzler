import { SingePickOption } from './singlepickoption';

export class SinglePickQuestion {
    constructor(
        public title: string,
        public text: string,
        public options: SingePickOption[]
    ) { };
    select(selectedOptionId: number) {
      this.options.forEach(o => o.isSelected = (o.id == selectedOptionId));
    }
}
