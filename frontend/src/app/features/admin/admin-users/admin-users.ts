import { Component, inject, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { AdminService } from '../../../core/services/admin.service';
import { User } from '../../../core/models/user';

@Component({
  selector: 'app-admin-users',
  imports: [CommonModule],
  templateUrl: './admin-users.html',
  styleUrl: './admin-users.scss'
})
export class AdminUsers implements OnInit {
  private adminService = inject(AdminService);
  users: User[] = [];
  filteredUsers: User[] = [];
  searchTerm = '';
  statusFilter = 'All Users';

  ngOnInit() {
    this.loadUsers();
  }

  loadUsers() {
    this.adminService.getAllUsers().subscribe({
      next: (users) => {
        this.users = users;
        this.filteredUsers = users;
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
                           (this.statusFilter === 'Active' && user.role !== 'BANNED') ||
                           (this.statusFilter === 'Banned' && user.role === 'BANNED');
      return matchesSearch && matchesStatus;
    });
  }

  formatDate(dateString: string): string {
    const date = new Date(dateString);
    return date.toLocaleDateString('en-US', { month: 'short', day: 'numeric', year: 'numeric' });
  }

  getStatusClass(user: User): string {
    return user.role === 'BANNED' ? 'banned' : 'active';
  }

  getStatusText(user: User): string {
    return user.role === 'BANNED' ? 'Banned' : 'Active';
  }

  banUser(user: User) {
    // TODO: Implement ban functionality
    console.log('Ban user:', user.username);
  }

  unbanUser(user: User) {
    // TODO: Implement unban functionality
    console.log('Unban user:', user.username);
  }

  deleteUser(user: User) {
    // TODO: Implement delete functionality
    console.log('Delete user:', user.username);
  }
}
