export class Option {
    public selected = false; 
    constructor(public text: string) { }

    toMyString(): string {
        return 'hugo';
    }

    toString(): string {
        console.log('In toString of option');
        return `${this.text}, checked -> ${this.selected}`;
    }
}