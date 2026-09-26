import { Component, signal } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { rxResource } from '@angular/core/rxjs-interop';
import { AdminService } from '../../core/services/admin.service';
import { Role } from '../../core/models/user.model';

@Component({
  selector: 'app-admin',
  imports: [CommonModule, FormsModule],
  templateUrl: './admin.html',
  styleUrl: './admin.css'
})
export class Admin {

  activeTab = signal<'users' | 'settings'>('users');

  usersResource = rxResource({
    stream: () => this.adminService.getAllUsers()
  });

  deadlineResource = rxResource({
    stream: () => this.adminService.getDeadline()
  });

  roles: Role[] = ['ROLE_PARTICIPANT', 'ROLE_JURY', 'ROLE_ADMIN'];

  newDeadline = signal('');
  actionError = signal<string | null>(null);
  actionSuccess = signal<string | null>(null);

  constructor(private adminService: AdminService) {}

  setTab(tab: 'users' | 'settings'): void {
    this.activeTab.set(tab);
  }

  changeRole(userId: number, newRole: Role): void {
  this.actionError.set(null);

  this.adminService.updateUserRole(userId, newRole).subscribe({
    next: () => {
      this.actionSuccess.set('Rôle mis à jour');
      this.usersResource.reload();
    },
    error: (err) => {
      this.actionError.set(err.error?.message || 'Erreur lors de la mise à jour');
    }
  });
}

  deleteUser(userId: number): void {
    if (!confirm('Supprimer cet utilisateur ?')) return;

    this.adminService.deleteUser(userId).subscribe({
      next: () => {
        this.actionSuccess.set('Utilisateur supprimé');
        this.usersResource.reload();
      },
      error: (err) => {
        this.actionError.set(err.error?.message || 'Erreur lors de la suppression');
      }
    });
  }

  updateDeadline(): void {
    if (!this.newDeadline()) return;

    this.actionError.set(null);
    this.actionSuccess.set(null);

    // Le champ <input type="datetime-local"> donne un format compatible avec LocalDateTime
    this.adminService.setDeadline({ submissionDeadline: this.newDeadline() }).subscribe({
      next: () => {
        this.actionSuccess.set('Deadline mise à jour');
        this.deadlineResource.reload();
      },
      error: (err) => {
        this.actionError.set(err.error?.message || 'Erreur lors de la mise à jour');
      }
    });
  }
}