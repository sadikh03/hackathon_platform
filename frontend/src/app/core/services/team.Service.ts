import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';
import { environment } from '../../../environments/environment';
import { TeamRequest, TeamResponse } from '../models/team.model';

@Injectable({
  providedIn: 'root'
})
export class TeamService {

  private apiUrl = `${environment.apiUrl}/teams`;

  constructor(private http: HttpClient) {}

  getAllTeams(): Observable<TeamResponse[]> {
    return this.http.get<TeamResponse[]>(this.apiUrl);
  }

  createTeam(request: TeamRequest): Observable<TeamResponse> {
    return this.http.post<TeamResponse>(this.apiUrl, request);
  }

  joinTeam(teamId: number): Observable<TeamResponse> {
    return this.http.post<TeamResponse>(`${this.apiUrl}/${teamId}/join`, {});
  }

  leaveTeam(teamId: number): Observable<string> {
    return this.http.post(`${this.apiUrl}/${teamId}/leave`, {}, { responseType: 'text' });
  }
}