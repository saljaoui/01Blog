import { Component, inject, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { AdminService } from '../../../core/services/admin.service';
import { User } from '../../../core/models/user';
import { ConfirmDeletePopup } from '../../../components/confirm-delete-popup/confirm-delete-popup';

@Component({
  selector: 'app-admin-users',
  imports: [CommonModule, ConfirmDeletePopup],
  templateUrl: './admin-users.html',
  styleUrl: './admin-users.scss'
})
export class AdminUsers implements OnInit {
  private adminService = inject(AdminService);
  users: User[] = [];
  filteredUsers: User[] = [];
  displayedUsers: User[] = [];
  searchTerm = '';
  statusFilter = 'All Users';
  displayedCount = 10;
  showDeletePopup = false;
  userToDelete: User | null = null;

  ngOnInit() {
    this.loadUsers();
  }

  loadUsers() {
    this.adminService.getAllUsers().subscribe({
      next: (users) => {
        console.log(">>>>>>>>>", users);
        this.users = users;
        this.filteredUsers = users;
        this.displayedUsers = this.filteredUsers.slice(0, this.displayedCount);
      },
      error: (error) => {
        console.error('Error loading users:', error);
      }
    });
  }

  onSearch(event: Event) {
    const target = event.target as HTMLInputElement;
    this.searchTerm = target.value.toLowerCase();
    this.applyFilters();
  }

  onStatusFilter(event: Event) {
    const target = event.target as HTMLSelectElement;
    this.statusFilter = target.value;
    this.applyFilters();
  }

  applyFilters() {
    this.filteredUsers = this.users.filter(user => {
      const matchesSearch = user.username.toLowerCase().includes(this.searchTerm) ||
        user.email.toLowerCase().includes(this.searchTerm);
      const matchesStatus = this.statusFilter === 'All Users' ||
        (this.statusFilter === 'Active' && user.status !== 'BANNED') ||
        (this.statusFilter === 'Banned' && user.status === 'BANNED');
      return matchesSearch && matchesStatus;
    });
    this.displayedCount = 10;
    this.displayedUsers = this.filteredUsers.slice(0, this.displayedCount);
  }

  loadMore() {
    this.displayedCount += 10;
    this.displayedUsers = this.filteredUsers.slice(0, this.displayedCount);
  }

  formatDate(dateString: string): string {
    const date = new Date(dateString);
    return date.toLocaleDateString('en-US', { month: 'short', day: 'numeric', year: 'numeric' });
  }

  getStatusClass(user: User): string {
    return user.status === 'BANNED' ? 'banned' : 'active';
  }

  getStatusText(user: User): string {
    return user.status === 'BANNED' ? 'Banned' : 'Active';
  }

  toggleUserStatus(user: User) {
    console.log('Unbanning user:', user.username);

    this.adminService.toggleUserStatus(user.id).subscribe({
      next: (data) => {
        // Update local user status
        user.status = data.status;
        console.log('User successfully unbanned:', data);
      },
      error: (err) => {
        console.error('Error unbanning user:', err);
      }
    });
  }

  deleteUser(user: User) {
    this.userToDelete = user;
    this.showDeletePopup = true;
  }

  confirmDeleteUser() {
    if (this.userToDelete) {
      this.adminService.deleteUser(this.userToDelete.id).subscribe({
        next: () => {
          // Remove user from local arrays
          this.users = this.users.filter(u => u.id !== this.userToDelete!.id);
          this.filteredUsers = this.filteredUsers.filter(u => u.id !== this.userToDelete!.id);
          this.displayedUsers = this.displayedUsers.filter(u => u.id !== this.userToDelete!.id);
          this.showDeletePopup = false;
          this.userToDelete = null;
        },
        error: (err) => {
          console.error('Error deleting user:', err);
          this.showDeletePopup = false;
          this.userToDelete = null;
        }
      });
    }
  }

  cancelDeleteUser() {
    this.showDeletePopup = false;
    this.userToDelete = null;
  }
}
