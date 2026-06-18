import { Component, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { ActivatedRoute } from '@angular/router';
import { DashboardService } from '../services/dashboard.service';
import { SessionDashboardData } from '../entities/session-dashboard-data';

@Component({
  selector: 'app-dashboard',
  standalone: true,
  imports: [CommonModule],
  templateUrl: './dashboard.component.html',
  styleUrl: './dashboard.component.css'
})
export class DashboardComponent implements OnInit {
  dashboardData?: SessionDashboardData;
  loading = true;
  error = false;

  constructor(
    private route: ActivatedRoute,
    private dashboardService: DashboardService
  ) {}

  ngOnInit(): void {
    const dashboardId = this.route.snapshot.paramMap.get('dashboardId');

    if (dashboardId) {
      this.dashboardService.getDashboardById(dashboardId).subscribe({
        next: (data) => {
          this.dashboardData = data;
          this.loading = false;
        },
        error: (err) => {
          console.error('Error loading dashboard:', err);
          this.error = true;
          this.loading = false;
        }
      });
    }
  }

  get totalAnswers(): number {
    if (!this.dashboardData) return 0;
    return this.dashboardData.correctAnswers + this.dashboardData.wrongAnswers;
  }

  get accuracyPercentage(): number {
    if (!this.dashboardData || this.totalAnswers === 0) return 0;
    return Math.round((this.dashboardData.correctAnswers / this.totalAnswers) * 100);
  }

  get averagePaymentAmount(): number {
    if (!this.dashboardData || this.dashboardData.numberOfPayments === 0) return 0;
    return this.dashboardData.paymentAmount / this.dashboardData.numberOfPayments;
  }

  formatCurrency(cents: number): string {
    return (cents / 100).toFixed(2);
  }
}
