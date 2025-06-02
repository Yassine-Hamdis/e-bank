import { Component, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { Router } from '@angular/router';
import { ClientSidebarComponent } from '../client-sidebar/client-sidebar.component';
import { AuthService } from '../../services/auth.service';
import { ClientService } from '../../services/client.service';
import { ClientNotification } from '../../models/client.model';

@Component({
  selector: 'app-client-notifications',
  imports: [CommonModule, FormsModule, ClientSidebarComponent],
  templateUrl: './client-notifications.component.html',
  styleUrl: './client-notifications.component.css'
})
export class ClientNotificationsComponent implements OnInit {
  notifications: ClientNotification[] = [];
  filteredNotifications: ClientNotification[] = [];
  isLoading = true;
  errorMessage = '';
  selectedFilter = 'all';
  selectedType = 'all';

  filterOptions = [
    { value: 'all', label: 'All Notifications' },
    { value: 'unread', label: 'Unread Only' },
    { value: 'read', label: 'Read Only' }
  ];

  typeOptions = [
    { value: 'all', label: 'All Types' },
    { value: 'TRANSACTION', label: 'Transactions' },
    { value: 'ACCOUNT', label: 'Account' },
    { value: 'SECURITY', label: 'Security' },
    { value: 'SYSTEM', label: 'System' },
    { value: 'PROMOTION', label: 'Promotions' }
  ];

  constructor(
    private authService: AuthService,
    private clientService: ClientService,
    private router: Router
  ) {}

  ngOnInit(): void {
    if (!this.authService.isAuthenticated() || !this.authService.isClient()) {
      this.router.navigate(['/login']);
      return;
    }

    this.loadNotifications();
  }

  loadNotifications(): void {
    this.isLoading = true;
    this.errorMessage = '';

    this.clientService.getNotifications().subscribe({
      next: (notifications) => {
        this.notifications = notifications.sort((a, b) =>
          new Date(b.createdDate).getTime() - new Date(a.createdDate).getTime()
        );
        this.applyFilters();
        this.isLoading = false;
      },
      error: (error) => {
        console.error('Error loading notifications:', error);
        this.errorMessage = 'Failed to load notifications. Please try again.';
        this.isLoading = false;
      }
    });
  }

  applyFilters(): void {
    let filtered = [...this.notifications];

    // Apply read/unread filter
    if (this.selectedFilter === 'unread') {
      filtered = filtered.filter(n => !n.isRead);
    } else if (this.selectedFilter === 'read') {
      filtered = filtered.filter(n => n.isRead);
    }

    // Apply type filter
    if (this.selectedType !== 'all') {
      filtered = filtered.filter(n => n.type === this.selectedType);
    }

    this.filteredNotifications = filtered;
  }

  onFilterChange(): void {
    this.applyFilters();
  }

  markAsRead(notification: ClientNotification): void {
    if (!notification.isRead) {
      this.clientService.markNotificationAsRead(notification.notificationId).subscribe({
        next: (updatedNotification) => {
          const index = this.notifications.findIndex(n => n.id === notification.id);
          if (index !== -1) {
            this.notifications[index] = updatedNotification;
            this.applyFilters();
          }
        },
        error: (error) => {
          console.error('Error marking notification as read:', error);
        }
      });
    }
  }

  markAllAsRead(): void {
    const unreadNotifications = this.notifications.filter(n => !n.isRead);

    unreadNotifications.forEach(notification => {
      this.clientService.markNotificationAsRead(notification.notificationId).subscribe({
        next: (updatedNotification) => {
          const index = this.notifications.findIndex(n => n.id === notification.id);
          if (index !== -1) {
            this.notifications[index] = updatedNotification;
          }
        },
        error: (error) => {
          console.error('Error marking notification as read:', error);
        }
      });
    });

    // Refresh the filtered list after a short delay
    setTimeout(() => {
      this.applyFilters();
    }, 1000);
  }

  deleteNotification(notification: ClientNotification): void {
    if (confirm('Are you sure you want to delete this notification?')) {
      this.clientService.deleteNotification(notification.notificationId).subscribe({
        next: () => {
          this.notifications = this.notifications.filter(n => n.id !== notification.id);
          this.applyFilters();
        },
        error: (error) => {
          console.error('Error deleting notification:', error);
        }
      });
    }
  }

  goBack(): void {
    this.router.navigate(['/client']);
  }

  formatDate(dateString: string): string {
    return new Date(dateString).toLocaleDateString('en-US', {
      year: 'numeric',
      month: 'long',
      day: 'numeric',
      hour: '2-digit',
      minute: '2-digit'
    });
  }

  getPriorityColor(priority: string): string {
    switch (priority) {
      case 'URGENT': return 'text-red-600 bg-red-50 border-red-200';
      case 'HIGH': return 'text-orange-600 bg-orange-50 border-orange-200';
      case 'NORMAL': return 'text-blue-600 bg-blue-50 border-blue-200';
      case 'LOW': return 'text-gray-600 bg-gray-50 border-gray-200';
      default: return 'text-gray-600 bg-gray-50 border-gray-200';
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

  get unreadCount(): number {
    return this.notifications.filter(n => !n.isRead).length;
  }
}
