import { SingePickOption } from "./singlepickoption";
import { SinglePickQuestion } from "./singlepickquestion";

describe('SinglepickComponent', () => {

  it.each([0,1,2,3])('should select exactly one option', (selectedOption) => {
    let instanceUnderTest = 
        new SinglePickQuestion(
            'Test Question',
            'This is instead of a meaninigful question text.',
            [
                new SingePickOption(0, 'Option 0'),
                new SingePickOption(1, 'Option 1'),
                new SingePickOption(2, 'Option 2'),
                new SingePickOption(3, 'Option 3'),
            ]);
    
    instanceUnderTest.select(selectedOption);
    
    instanceUnderTest.options.forEach(
        (option,index) => expect(option.isSelected).toEqual(selectedOption == index));
  });
  it ('should reset previous selected option when another option is selected', () => {
    let instanceUnderTest = 
        new SinglePickQuestion(
            'Test Question',
            'This is instead of a meaninigful question text.',
            [
                new SingePickOption(0, 'Option 0'),
                new SingePickOption(1, 'Option 1'),
                new SingePickOption(2, 'Option 2'),
                new SingePickOption(3, 'Option 3'),
            ]);
    
    instanceUnderTest.select(0);
    instanceUnderTest.select(1);
    
    instanceUnderTest.options.forEach(
        (option,index) => expect(option.isSelected).toEqual(1 == index));
  });
  it.each([0,1,2,3]) ('should toggle already selected option when clicked twice', (selection) => {
    let instanceUnderTest = 
        new SinglePickQuestion(
            'Test Question',
            'This is instead of a meaninigful question text.',
            [
                new SingePickOption(0, 'Option 0'),
                new SingePickOption(1, 'Option 1'),
                new SingePickOption(2, 'Option 2'),
                new SingePickOption(3, 'Option 3'),
            ]);
    
    instanceUnderTest.select(selection);
    instanceUnderTest.select(selection);
    
    instanceUnderTest.options.forEach(
        (option,index) => expect(option.isSelected).toEqual(false));
  });
});