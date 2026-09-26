import { Component, signal } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { rxResource } from '@angular/core/rxjs-interop';
import { ProjectService } from '../../core/services/project.service';
import { AuthService } from '../../core/services/auth.service';
import { EvaluationService } from '../../core/services/evaluation.service';
import { AdminService } from '../../core/services/admin.service';
import { ProjectRequest, ProjectResponse } from '../../core/models/project.model';
import { EvaluationRequest } from '../../core/models/evaluation.model';

@Component({
  selector: 'app-projects',
  imports: [CommonModule, FormsModule],
  templateUrl: './projects.html',
  styleUrl: './projects.css'
})
export class Projects {

  projectsResource = rxResource({
    stream: () => this.projectService.getAllProjects()
  });

  myEvaluationsResource = rxResource({
    stream: () => this.evaluationService.getMyEvaluations()
  });

  // ---- Formulaire (participant uniquement) ----
  formData = signal<ProjectRequest>({ title: '', description: '', githubLink: '' });
  editingProjectId = signal<number | null>(null);

  // ---- Upload de fichier (participant uniquement) ----
  selectedFile = signal<File | null>(null);
  lastCreatedProjectId = signal<number | null>(null);

  // ---- Modale d'évaluation (jury uniquement) ----
  evaluatingProject = signal<ProjectResponse | null>(null);
  evalForm = signal<EvaluationRequest>({
    innovationScore: 5, technicalScore: 5, presentationScore: 5, comment: ''
  });

  actionError = signal<string | null>(null);
  actionSuccess = signal<string | null>(null);

  constructor(
    private projectService: ProjectService,
    private evaluationService: EvaluationService,
    private adminService: AdminService,
    public authService: AuthService
  ) {}

  // ---- Participant ----

  submitProject(): void {
    this.actionError.set(null);
    this.actionSuccess.set(null);

    const request = this.formData();
    const editingId = this.editingProjectId();

    const action$ = editingId
      ? this.projectService.updateProject(editingId, request)
      : this.projectService.submitProject(request);

    action$.subscribe({
      next: (response) => {
        this.actionSuccess.set(editingId ? 'Projet mis à jour !' : 'Projet soumis avec succès !');
        this.lastCreatedProjectId.set(response.id); // ← on retient l'id, qu'il vienne de créer ou modifier
        this.cancelEdit();
        this.projectsResource.reload();
      },
      error: (err) => {
        this.actionError.set(err.error?.message || 'Une erreur est survenue');
      }
    });
  }

  startEdit(project: ProjectResponse): void {
    this.editingProjectId.set(project.id);
    this.formData.set({
      title: project.title,
      description: project.description,
      githubLink: project.githubLink || ''
    });
  }

  cancelEdit(): void {
    this.editingProjectId.set(null);
    this.formData.set({ title: '', description: '', githubLink: '' });
  }

  updateField<K extends keyof ProjectRequest>(field: K, value: ProjectRequest[K]): void {
    this.formData.update(current => ({ ...current, [field]: value }));
  }

  // ---- Jury ----

  isEvaluated(projectId: number): boolean {
    return (this.myEvaluationsResource.value() ?? []).some(e => e.projectId === projectId);
  }

  openEvaluation(project: ProjectResponse): void {
    this.actionError.set(null);
    this.actionSuccess.set(null);

    const existing = (this.myEvaluationsResource.value() ?? [])
      .find(e => e.projectId === project.id);

    this.evalForm.set(existing
      ? {
          innovationScore: existing.innovationScore,
          technicalScore: existing.technicalScore,
          presentationScore: existing.presentationScore,
          comment: existing.comment || ''
        }
      : { innovationScore: 5, technicalScore: 5, presentationScore: 5, comment: '' }
    );

    this.evaluatingProject.set(project);
  }

  closeEvaluation(): void {
    this.evaluatingProject.set(null);
  }

  updateEvalField<K extends keyof EvaluationRequest>(field: K, value: EvaluationRequest[K]): void {
    this.evalForm.update(current => ({ ...current, [field]: value }));
  }

  submitEvaluation(): void {
    const project = this.evaluatingProject();
    if (!project) return;

    this.evaluationService.evaluate(project.id, this.evalForm()).subscribe({
      next: () => {
        this.actionSuccess.set('Évaluation enregistrée !');
        this.myEvaluationsResource.reload();
        this.closeEvaluation();
      },
      error: (err) => {
        this.actionError.set(err.error?.message || 'Erreur lors de la notation');
      }
    });
  }

  // ---- Admin ----

  deleteProject(projectId: number): void {
    if (!confirm('Supprimer ce projet ?')) return;

    this.adminService.deleteProject(projectId).subscribe({
      next: () => {
        this.actionSuccess.set('Projet supprimé');
        this.projectsResource.reload();
      },
      error: (err) => {
        this.actionError.set(err.error?.message || 'Erreur lors de la suppression');
      }
    });
  }


  onFileSelected(event: Event): void {
    const input = event.target as HTMLInputElement;
    if (input.files && input.files.length > 0) {
      this.selectedFile.set(input.files[0]);
    }
  }

  uploadFile(projectId: number): void {
    const file = this.selectedFile();
    if (!file) return;

    this.projectService.uploadFile(projectId, file).subscribe({
      next: () => {
        this.actionSuccess.set('Fichier envoyé !');
        this.selectedFile.set(null);
        this.projectsResource.reload();
      },
      error: (err) => {
        this.actionError.set(err.error?.message || 'Erreur lors de l\'envoi du fichier');
      }
    });
  }

  getDownloadUrl(projectId: number): string {
    return this.projectService.getFileDownloadUrl(projectId);
  }

  deleteFile(projectId: number): void {
    if (!confirm('Supprimer le fichier attaché ?')) return;

    this.actionError.set(null);
    this.actionSuccess.set(null);

    this.projectService.deleteFile(projectId).subscribe({
      next: () => {
        this.actionSuccess.set('Fichier supprimé');
        this.projectsResource.reload();
      },
      error: (err) => {
        this.actionError.set(err.error?.message || 'Erreur lors de la suppression du fichier');
      }
    });
  }
}