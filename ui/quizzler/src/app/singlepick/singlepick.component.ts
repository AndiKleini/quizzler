import { Component, OnInit } from '@angular/core';
import {FormGroup, ReactiveFormsModule, FormBuilder, FormArray} from '@angular/forms';

@Component({
  selector: 'quizzler-singlepick',
  standalone: true,
  imports: [ ReactiveFormsModule ],
  templateUrl: './singlepick.component.html',
  styleUrl: './singlepick.component.css'
})
export class SinglepickComponent implements OnInit  {
  selectedIndex = -1;
  singlePickForm: FormGroup;
  constructor(private formBuilder: FormBuilder) {
    this.singlePickForm = this.formBuilder.group( {
      options: this.formBuilder.array([])
    });
  }
  ngOnInit(): void {
    // add for each option a line with a checkbox
    this.options.push(this.formBuilder.control('0'));
    this.options.push(this.formBuilder.control('1'));
    this.options.push(this.formBuilder.control('2'));
  }
  get options() {
    return this.singlePickForm.get('options') as FormArray;
  }
}
