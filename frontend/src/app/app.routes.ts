import { Routes } from '@angular/router';
import { authGuard } from './core/guards/auth.guard';
import { roleGuard } from './core/guards/role.guard';

export const routes: Routes = [
  {
    path: 'login',
    loadComponent: () => import('./features/auth/login/login').then(m => m.Login)
  },
  {
    path: 'register',
    loadComponent: () => import('./features/auth/register/register').then(m => m.Register)
  },
  {
    path: 'teams',
    canActivate: [authGuard],
    loadComponent: () => import('./features/teams/teams').then(m => m.Teams)
  },
  {
    path: 'projects',
    canActivate: [authGuard],
    loadComponent: () => import('./features/projects/projects').then(m => m.Projects)
  },
  {
    path: 'leaderboard',
    canActivate: [authGuard],
    loadComponent: () => import('./features/leaderboard/leaderboard').then(m => m.Leaderboard)
  },
  {
    path: 'admin',
    canActivate: [authGuard, roleGuard('ROLE_ADMIN')],
    loadComponent: () => import('./features/admin/admin').then(m => m.Admin)
  },
  {
    path: '',
    redirectTo: 'teams',
    pathMatch: 'full'
  },
  {
    path: '**',
    redirectTo: 'teams'
  }
];