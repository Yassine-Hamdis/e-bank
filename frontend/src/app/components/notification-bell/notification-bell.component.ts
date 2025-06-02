import { Component, OnInit, OnDestroy } from '@angular/core';
import { CommonModule } from '@angular/common';
import { Router } from '@angular/router';
import { Subscription, interval } from 'rxjs';
import { ClientService } from '../../services/client.service';
import { ClientNotification } from '../../models/client.model';

@Component({
  selector: 'app-notification-bell',
  imports: [CommonModule],
  templateUrl: './notification-bell.component.html',
  styleUrl: './notification-bell.component.css'
})
export class NotificationBellComponent implements OnInit, OnDestroy {
  notifications: ClientNotification[] = [];
  unreadCount = 0;
  isDropdownOpen = false;
  isLoading = false;
  private notificationSubscription?: Subscription;
  private refreshSubscription?: Subscription;

  constructor(
    private clientService: ClientService,
    private router: Router
  ) {}

  ngOnInit(): void {
    this.loadNotifications();
    // Refresh notifications every 30 seconds
    this.refreshSubscription = interval(30000).subscribe(() => {
      this.loadNotifications();
    });
  }

  ngOnDestroy(): void {
    if (this.notificationSubscription) {
      this.notificationSubscription.unsubscribe();
    }
    if (this.refreshSubscription) {
      this.refreshSubscription.unsubscribe();
    }
  }

  loadNotifications(): void {
    this.isLoading = true;
    this.notificationSubscription = this.clientService.getNotifications().subscribe({
      next: (notifications) => {
        this.notifications = notifications.sort((a, b) =>
          new Date(b.createdDate).getTime() - new Date(a.createdDate).getTime()
        );
        this.unreadCount = notifications.filter(n => !n.isRead).length;
        this.isLoading = false;
      },
      error: (error) => {
        console.error('Error loading notifications:', error);
        this.isLoading = false;
      }
    });
  }

  toggleDropdown(): void {
    this.isDropdownOpen = !this.isDropdownOpen;
  }

  closeDropdown(): void {
    this.isDropdownOpen = false;
  }

  markAsRead(notification: ClientNotification): void {
    if (!notification.isRead) {
      this.clientService.markNotificationAsRead(notification.notificationId).subscribe({
        next: (updatedNotification) => {
          const index = this.notifications.findIndex(n => n.id === notification.id);
          if (index !== -1) {
            this.notifications[index] = updatedNotification;
            this.unreadCount = this.notifications.filter(n => !n.isRead).length;
          }
        },
        error: (error) => {
          console.error('Error marking notification as read:', error);
        }
      });
    }
  }

  viewAllNotifications(): void {
    this.closeDropdown();
    this.router.navigate(['/client/notifications']);
  }

  getRecentNotifications(): ClientNotification[] {
    return this.notifications.slice(0, 5);
  }

  formatDate(dateString: string): string {
    const date = new Date(dateString);
    const now = new Date();
    const diffInMinutes = Math.floor((now.getTime() - date.getTime()) / (1000 * 60));

    if (diffInMinutes < 1) return 'Just now';
    if (diffInMinutes < 60) return `${diffInMinutes}m ago`;
    if (diffInMinutes < 1440) return `${Math.floor(diffInMinutes / 60)}h ago`;
    return `${Math.floor(diffInMinutes / 1440)}d ago`;
  }

  getPriorityColor(priority: string): string {
    switch (priority) {
      case 'URGENT': return 'text-red-600';
      case 'HIGH': return 'text-orange-600';
      case 'NORMAL': return 'text-blue-600';
      case 'LOW': return 'text-gray-600';
      default: return 'text-gray-600';
    }
  }

  getTypeIcon(type: string): string {
    switch (type) {
      case 'TRANSACTION': return '💳';
      case 'ACCOUNT': return '🏦';
      case 'SECURITY': return '🔒';
      case 'SYSTEM': return '⚙️';
      case 'PROMOTION': return '🎁';
      default: return '📢';
    }
  }
}
