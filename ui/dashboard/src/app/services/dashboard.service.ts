import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';
import { environment } from '../../environments/environment';
import { SessionDashboardData } from '../entities/session-dashboard-data';

@Injectable({
  providedIn: 'root'
})
export class DashboardService {
  private apiBaseUrl = environment.apiBaseUrl;

  constructor(private http: HttpClient) { }

  getDashboardById(dashboardId: string): Observable<SessionDashboardData> {
    return this.http.get<SessionDashboardData>(`${this.apiBaseUrl}/api/SessionDashboard/${dashboardId}`);
  }
}
