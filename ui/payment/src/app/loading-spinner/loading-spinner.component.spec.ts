import { ComponentFixture, TestBed } from '@angular/core/testing';
import { LoadingSpinnerComponent } from './loading-spinner.component';

describe('LoadingSpinnerComponent', () => {
  let component: LoadingSpinnerComponent;
  let fixture: ComponentFixture<LoadingSpinnerComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [LoadingSpinnerComponent]
    }).compileComponents();

    fixture = TestBed.createComponent(LoadingSpinnerComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });

  it('should render spinner element', () => {
    const compiled = fixture.nativeElement as HTMLElement;
    expect(compiled.querySelector('.spinner')).toBeTruthy();
  });

  it('should render spinner inside container', () => {
    const compiled = fixture.nativeElement as HTMLElement;
    const container = compiled.querySelector('.spinner-container');
    expect(container).toBeTruthy();
    expect(container?.querySelector('.spinner')).toBeTruthy();
  });
});
