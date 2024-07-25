import { ComponentFixture, TestBed } from '@angular/core/testing';

import { SinglePickComponent } from './single-pick.component';

describe('SinglePickComponent', () => {
  let component: SinglePickComponent;
  let fixture: ComponentFixture<SinglePickComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [SinglePickComponent]
    })
    .compileComponents();
    
    fixture = TestBed.createComponent(SinglePickComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
