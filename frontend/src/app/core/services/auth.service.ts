import { Injectable, signal } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Router } from '@angular/router';
import { Observable, tap } from 'rxjs';
import { environment } from '../../../environments/environment';
import { LoginRequest, RegisterRequest, JwtResponse, Role } from '../models/user.model';

@Injectable({ //creer et injectable (root) dans tout le projet
  providedIn: 'root' 
})
export class AuthService {

  private apiUrl = `${environment.apiUrl}/auth`;

  // Signal qui contient le username connecté (ou null si pas connecté)
  // "readonly" à l'extérieur : les autres composants peuvent LIRE mais pas modifier directement
  currentUsername = signal<string | null>(this.getStoredUsername());
  currentRole = signal<Role | null>(this.getStoredRole());

  constructor(private http: HttpClient, private router: Router) {}

  register(request: RegisterRequest): Observable<string> {
    return this.http.post(`${this.apiUrl}/register`, request, { responseType: 'text' });
  }

  login(request: LoginRequest): Observable<JwtResponse> {
    return this.http.post<JwtResponse>(`${this.apiUrl}/login`, request).pipe(
      tap(response => {
        // "tap" = exécute un effet de bord SANS modifier la donnée qui transite dans l'Observable
        this.storeSession(response);
      })
    );
  }

  logout(): void {
    localStorage.removeItem('token');
    localStorage.removeItem('username');
    localStorage.removeItem('role');
    this.currentUsername.set(null);
    this.currentRole.set(null);
    this.router.navigate(['/login']);
  }

  getToken(): string | null {
    return localStorage.getItem('token');
  }

  isLoggedIn(): boolean {
    return this.getToken() !== null;
  }

  hasRole(role: Role): boolean {
    return this.currentRole() === role;
  }

  private storeSession(response: JwtResponse): void {
    localStorage.setItem('token', response.token);
    localStorage.setItem('username', response.username);
    localStorage.setItem('role', response.role);
    this.currentUsername.set(response.username);
    this.currentRole.set(response.role);
  }

  private getStoredUsername(): string | null {
    return localStorage.getItem('username');
  }

  private getStoredRole(): Role | null {
    return localStorage.getItem('role') as Role | null;
  }
}