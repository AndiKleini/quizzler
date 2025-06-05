import { ComponentFixture, TestBed } from '@angular/core/testing';
import { SinglepickComponent } from './singlepick.component';

describe('SinglepickComponent', () => {
  let component: SinglepickComponent;
  let fixture: ComponentFixture<SinglepickComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [SinglepickComponent]
    })
    .compileComponents();

    fixture = TestBed.createComponent(SinglepickComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });

  it ('should display initally 3 options', () => {
    expect(component.singlePickForm).toBeTruthy();
  });
});
