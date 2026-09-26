import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';
import { environment } from '../../../environments/environment';
import { ProjectRequest, ProjectResponse } from '../models/project.model';

@Injectable({
  providedIn: 'root'
})
export class ProjectService {

  private apiUrl = `${environment.apiUrl}/projects`;

  constructor(private http: HttpClient) {}

  getAllProjects(): Observable<ProjectResponse[]> {
    return this.http.get<ProjectResponse[]>(this.apiUrl);
  }

  submitProject(request: ProjectRequest): Observable<ProjectResponse> {
    return this.http.post<ProjectResponse>(this.apiUrl, request);
  }

  updateProject(id: number, request: ProjectRequest): Observable<ProjectResponse> {
    return this.http.put<ProjectResponse>(`${this.apiUrl}/${id}`, request);
  }
  uploadFile(projectId: number, file: File): Observable<ProjectResponse> {
    const formData = new FormData();
    formData.append('file', file);
    return this.http.post<ProjectResponse>(`${this.apiUrl}/${projectId}/file`, formData);
  }

  getFileDownloadUrl(projectId: number): string {
    return `${this.apiUrl}/${projectId}/file`;
  }

  deleteFile(projectId: number): Observable<ProjectResponse> {
    return this.http.delete<ProjectResponse>(`${this.apiUrl}/${projectId}/file`);
  }
}