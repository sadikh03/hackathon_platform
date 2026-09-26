import { Component, signal } from '@angular/core';
import { CommonModule } from '@angular/common';
import { RouterLink, RouterLinkActive } from '@angular/router';
import { rxResource } from '@angular/core/rxjs-interop';
import { AuthService } from '../../core/services/auth.service';
import { NotificationService } from '../../core/services/notification.service';

@Component({
  selector: 'app-sidebar',
  imports: [CommonModule, RouterLink, RouterLinkActive],
  templateUrl: './sidebar.html',
  styleUrl: './sidebar.css'
})
export class Sidebar {

  notificationsResource = rxResource({
    stream: () => this.notificationService.getMyNotifications()
  });

  unreadCountResource = rxResource({
    stream: () => this.notificationService.getUnreadCount()
  });

  showPanel = signal(false);

  constructor(
    public authService: AuthService,
    private notificationService: NotificationService
  ) {}

  togglePanel(): void {
    this.showPanel.update(v => !v);
  }

  markAsRead(id: number): void {
    this.notificationService.markAsRead(id).subscribe({
      next: () => {
        this.notificationsResource.reload();
        this.unreadCountResource.reload();
      }
    });
  }

  markAllAsRead(): void {
    this.notificationService.markAllAsRead().subscribe({
      next: () => {
        this.notificationsResource.reload();
        this.unreadCountResource.reload();
      }
    });
  }

  onLogout(): void {
    this.authService.logout();
  }
}