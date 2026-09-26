import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';
import { environment } from '../../../environments/environment';
import { UserResponse, Role } from '../models/user.model';
import { DeadlineRequest, DeadlineResponse } from '../models/settings.model';

@Injectable({
  providedIn: 'root'
})
export class AdminService {

  private apiUrl = `${environment.apiUrl}/admin`;
  private settingsUrl = `${environment.apiUrl}/settings`;

  constructor(private http: HttpClient) {}

  getAllUsers(): Observable<UserResponse[]> {
    return this.http.get<UserResponse[]>(`${this.apiUrl}/users`);
  }

  updateUserRole(userId: number, role: Role): Observable<UserResponse> {
    return this.http.put<UserResponse>(`${this.apiUrl}/users/${userId}/role`, { role });
  }

  deleteUser(userId: number): Observable<string> {
    return this.http.delete(`${this.apiUrl}/users/${userId}`, { responseType: 'text' });
  }

  deleteTeam(teamId: number): Observable<string> {
    return this.http.delete(`${this.apiUrl}/teams/${teamId}`, { responseType: 'text' });
  }

  deleteProject(projectId: number): Observable<string> {
    return this.http.delete(`${this.apiUrl}/projects/${projectId}`, { responseType: 'text' });
  }

  getDeadline(): Observable<DeadlineResponse> {
    return this.http.get<DeadlineResponse>(`${this.settingsUrl}/deadline`);
  }

  setDeadline(request: DeadlineRequest): Observable<DeadlineResponse> {
    return this.http.put<DeadlineResponse>(`${this.apiUrl}/settings/deadline`, request);
  }
}