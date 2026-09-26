import { inject } from '@angular/core';
import { CanActivateFn, Router } from '@angular/router';
import { AuthService } from '../services/auth.service';
import { Role } from '../models/user.model';

export function roleGuard(allowedRole: Role): CanActivateFn {
  return () => {

    const authService = inject(AuthService);
    const router = inject(Router);

    if (authService.isLoggedIn() && authService.hasRole(allowedRole)) {
      return true;
    }

    router.navigate(['/dashboard']); // redirige vers une page neutre plutôt que login (déjà connecté, juste pas le bon rôle)
    return false;
  };
}