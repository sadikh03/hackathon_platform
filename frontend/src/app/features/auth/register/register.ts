import { Component, signal } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { Router, RouterLink } from '@angular/router';
import { AuthService } from '../../../core/services/auth.service';
import { RegisterRequest, Role } from '../../../core/models/user.model';

@Component({
  selector: 'app-register',
  imports: [CommonModule, FormsModule, RouterLink],
  templateUrl: './register.html',
  styleUrl: './register.css'
})
export class Register {

  formData: RegisterRequest = {
    username: '',
    email: '',
    password: '',
    role: 'ROLE_PARTICIPANT'
  };

  roles: Role[] = ['ROLE_PARTICIPANT', 'ROLE_JURY', 'ROLE_ADMIN'];

  errorMessage = signal<string | null>(null);
  successMessage = signal<string | null>(null);
  isLoading = signal(false);

  constructor(private authService: AuthService, private router: Router) {}

  onSubmit(): void {
    this.errorMessage.set(null);
    this.successMessage.set(null);
    this.isLoading.set(true);

    this.authService.register(this.formData).subscribe({
      next: () => {
        this.isLoading.set(false);
        this.successMessage.set('Compte créé avec succès ! Redirection...');
        setTimeout(() => this.router.navigate(['/login']), 1500);
      },
      error: (err) => {
        this.isLoading.set(false);
        this.errorMessage.set(err.error?.message || 'Erreur lors de l\'inscription');
      }
    });
  }
}