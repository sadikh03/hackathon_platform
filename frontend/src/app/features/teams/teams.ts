import { Component, signal } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { rxResource } from '@angular/core/rxjs-interop';
import { TeamService } from '../../core/services/team.Service';
import { AuthService } from '../../core/services/auth.service';
import { TeamRequest } from '../../core/models/team.model';

@Component({
  selector: 'app-teams',
  imports: [CommonModule, FormsModule],
  templateUrl: './teams.html',
  styleUrl: './teams.css'
})
export class Teams {

  // RxResource : charge automatiquement la liste des équipes au démarrage du composant
  teamsResource = rxResource({
    stream: () => this.teamService.getAllTeams()
  });

  newTeamName = signal('');
  actionError = signal<string | null>(null);

  constructor(
    private teamService: TeamService,
    public authService: AuthService // public pour y accéder directement dans le template
  ) {}

  createTeam(): void {
    if (!this.newTeamName().trim()) return;

    this.actionError.set(null);
    const request: TeamRequest = { name: this.newTeamName() };

    this.teamService.createTeam(request).subscribe({
      next: () => {
        this.newTeamName.set('');
        this.teamsResource.reload(); // ← on redemande la liste à jour après création
      },
      error: (err) => {
        this.actionError.set(err.error?.message || 'Erreur lors de la création');
      }
    });
  }

  isUserInAnyTeam(): boolean {
    const teams = this.teamsResource.value() ?? [];
    const username = this.authService.currentUsername();
    return teams.some(team => team.members.some(m => m.username === username));
  }

  canJoinTeam(members: { username: string }[]): boolean {
    if (!this.authService.hasRole('ROLE_PARTICIPANT')) {
      return false;
    }
    return !this.isUserInAnyTeam();
  }

  joinTeam(teamId: number): void {
    this.actionError.set(null);

    this.teamService.joinTeam(teamId).subscribe({
      next: () => this.teamsResource.reload(),
      error: (err) => {
        this.actionError.set(err.error?.message || 'Erreur lors de l\'ajout');
      }
    });
  }

  leaveTeam(teamId: number): void {
    this.actionError.set(null);

    this.teamService.leaveTeam(teamId).subscribe({
      next: () => this.teamsResource.reload(),
      error: (err) => {
        this.actionError.set(err.error?.message || 'Erreur lors du départ');
      }
    });
  }

  isCurrentUserMember(members: { username: string }[]): boolean {
    return members.some(m => m.username === this.authService.currentUsername());
  }
}