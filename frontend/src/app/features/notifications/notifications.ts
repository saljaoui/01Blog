import { Component, OnInit } from '@angular/core';
import { SidebarRight } from '../../components/sidebar-right/sidebar-right';
import { NotificationService, NotificationResponse } from '../../core/services/notification.service';
import { CommonModule } from '@angular/common';
import { DateUtilsService } from '../../core/services/utils/DateUtil.service';

@Component({
  selector: 'app-notifications',
  imports: [SidebarRight, CommonModule],
  templateUrl: './notifications.html',
  styleUrl: './notifications.scss'
})
export class Notifications implements OnInit {
  notifications: NotificationResponse[] = [];
  unreadCount: number = 0;

  constructor(
    private notificationService: NotificationService,
    private dateUtils: DateUtilsService
  ) { }

  ngOnInit() {
    this.loadNotifications();
    this.loadUnreadCount();
  }

  loadNotifications() {
    this.notificationService.getNotifications().subscribe({
      next: (notifications) => {
        this.notifications = notifications;
        console.log("this.notifications", this.notifications);

      },
      error: (error) => {
        console.error('Error loading notifications:', error);
      }
    });
  }

  loadUnreadCount() {
    this.notificationService.getUnreadCount().subscribe({
      next: (count) => {
        this.unreadCount = count;
      },
      error: (error) => {
        console.error('Error loading unread count:', error);
      }
    });
  }

  markAsRead(notificationId: number) {
    this.notificationService.markAsRead(notificationId).subscribe({
      next: () => {
        // Update local state
        const notification = this.notifications.find(n => n.id === notificationId);
        if (notification) {
          if (!notification.read) {
            this.unreadCount = Math.max(0, this.unreadCount - 1);
          }
          notification.read = true;
        }
        // Update the service's unread count
        this.notificationService.updateUnreadCount(this.unreadCount);
      },
      error: (error) => {
        console.error('Error marking notification as read:', error);
      }
    });
  }

  formatDate(dateString: string): string {
    return this.dateUtils.formatDate(dateString);
  }
}
