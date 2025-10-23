import { Component, HostListener, inject, signal, Output, EventEmitter } from '@angular/core';
import { RouterLink } from '@angular/router';
import { AuthService } from '../../core/services/auth.service';

@Component({
  selector: 'app-header',
  imports: [RouterLink],
  templateUrl: './header.html',
  styleUrl: './header.scss'
})
export class Header {
  isOpen = signal(false);
  isNotificationsOpen = signal(false);
  private authService = inject(AuthService);

  @Output() toggleSidebar = new EventEmitter<void>();

  toggleDropdown() {
    this.isOpen.update((v: boolean) => !v);
    // Close notifications when opening profile dropdown
    if (this.isOpen()) {
      this.isNotificationsOpen.set(false);
    }
    console.log(this.isOpen());
  }

  toggleNotifications() {
    this.isNotificationsOpen.update((v: boolean) => !v);
    // Close profile dropdown when opening notifications
    if (this.isNotificationsOpen()) {
      this.isOpen.set(false);
    }
    console.log('Notifications open:', this.isNotificationsOpen());
  }

  onToggleSidebar() {
    this.toggleSidebar.emit();
  }

  @HostListener('document:click', ['$event'])
  closeDropdown(event: MouseEvent) {
    const target = event.target as HTMLElement;

    // Close profile dropdown if click is outside
    if (!target.closest('.dropdown')) {
      this.isOpen.set(false);
    }

    // Close notifications dropdown if click is outside
    if (!target.closest('.notifications-dropdown')) {
      this.isNotificationsOpen.set(false);
    }

    // Close sidebar if click is outside on mobile - handled by parent component
  }

  logout() {
    console.log('Logout clicked');
    this.isOpen.set(false);
    console.log('Logging out user');
    this.authService.logout();
  }
}
