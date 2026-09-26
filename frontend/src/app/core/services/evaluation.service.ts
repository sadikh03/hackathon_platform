import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';
import { environment } from '../../../environments/environment';
import { ProjectResponse } from '../models/project.model';
import { EvaluationRequest, EvaluationResponse } from '../models/evaluation.model';

@Injectable({
  providedIn: 'root'
})
export class EvaluationService {

  private apiUrl = `${environment.apiUrl}/jury`;

  constructor(private http: HttpClient) {}

  getProjectsToEvaluate(): Observable<ProjectResponse[]> {
    return this.http.get<ProjectResponse[]>(`${this.apiUrl}/projects`);
  }

  evaluate(projectId: number, request: EvaluationRequest): Observable<EvaluationResponse> {
    return this.http.post<EvaluationResponse>(`${this.apiUrl}/evaluations/${projectId}`, request);
  }
  getMyEvaluations(): Observable<EvaluationResponse[]> {
  return this.http.get<EvaluationResponse[]>(`${this.apiUrl}/evaluations/mine`);
}
}