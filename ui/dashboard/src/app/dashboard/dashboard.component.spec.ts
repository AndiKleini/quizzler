import { ComponentFixture, TestBed } from '@angular/core/testing';
import { ActivatedRoute } from '@angular/router';
import { of, throwError } from 'rxjs';
import { DashboardComponent } from './dashboard.component';
import { DashboardService } from '../services/dashboard.service';
import { SessionDashboardData } from '../entities/session-dashboard-data';

describe('DashboardComponent', () => {
  let component: DashboardComponent;
  let fixture: ComponentFixture<DashboardComponent>;
  let mockDashboardService: jasmine.SpyObj<DashboardService>;
  let mockActivatedRoute: any;

  const mockDashboardData: SessionDashboardData = {
    id: 1,
    dashboardId: '11111111-2222-3333-4444-555555555555',
    paymentAmount: 1500,
    numberOfPayments: 3,
    wrongAnswers: 2,
    correctAnswers: 8,
    questions: 10
  };

  beforeEach(async () => {
    mockDashboardService = jasmine.createSpyObj('DashboardService', ['getDashboardById']);
    mockActivatedRoute = {
      snapshot: {
        paramMap: {
          get: jasmine.createSpy('get').and.returnValue('11111111-2222-3333-4444-555555555555')
        }
      }
    };

    await TestBed.configureTestingModule({
      imports: [DashboardComponent],
      providers: [
        { provide: DashboardService, useValue: mockDashboardService },
        { provide: ActivatedRoute, useValue: mockActivatedRoute }
      ]
    }).compileComponents();

    fixture = TestBed.createComponent(DashboardComponent);
    component = fixture.componentInstance;
  });

  it('should create', () => {
    mockDashboardService.getDashboardById.and.returnValue(of(mockDashboardData));
    fixture.detectChanges();
    expect(component).toBeTruthy();
  });

  it('should load dashboard data on init', () => {
    mockDashboardService.getDashboardById.and.returnValue(of(mockDashboardData));
    fixture.detectChanges();

    expect(component.dashboardData).toEqual(mockDashboardData);
    expect(component.loading).toBeFalse();
    expect(component.error).toBeFalse();
  });

  it('should handle error when loading dashboard data', () => {
    mockDashboardService.getDashboardById.and.returnValue(
      throwError(() => new Error('API Error'))
    );
    fixture.detectChanges();

    expect(component.error).toBeTrue();
    expect(component.loading).toBeFalse();
  });

  it('should calculate total answers correctly', () => {
    component.dashboardData = mockDashboardData;
    expect(component.totalAnswers).toBe(10);
  });

  it('should calculate accuracy percentage correctly', () => {
    component.dashboardData = mockDashboardData;
    expect(component.accuracyPercentage).toBe(80);
  });

  it('should format currency correctly', () => {
    expect(component.formatCurrency(1500)).toBe('15.00');
    expect(component.formatCurrency(2550)).toBe('25.50');
  });
});
