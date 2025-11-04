import { Component, inject, OnInit } from '@angular/core';
import { UserService } from '../../core/services/user.service';
import { User } from '../../core/models/user';
import { CommonModule } from '@angular/common';
import { RouterLink } from '@angular/router';
import { FormsModule } from '@angular/forms';

@Component({
  selector: 'app-sidebar-right',
  imports: [CommonModule, FormsModule],
  templateUrl: './sidebar-right.html',
  styleUrl: './sidebar-right.scss'
})
export class SidebarRight implements OnInit {
  private userService = inject(UserService);

  searchQuery: string = '';
  users: User[] = [];
  isSearching: boolean = false;

  ngOnInit(): void {
    // Load default users or popular users
    this.loadDefaultUsers();
  }

  loadDefaultUsers(): void {
    // For now, we'll load some default users or leave empty
    // In a real app, you might load popular users or recent users
    this.users = [];
  }

  onSearch(): void {
    if (!this.searchQuery.trim()) {
      this.loadDefaultUsers();
      return;
    }

    this.isSearching = true;
    this.userService.searchUsers(this.searchQuery.trim()).subscribe({
      next: (users) => {
        this.users = users;
        this.isSearching = false;
      },
      error: (err) => {
        console.error('Error searching users:', err);
        this.isSearching = false;
      }
    });
  }

  onSearchInput(event: any): void {
    this.searchQuery = event.target.value;
    if (this.searchQuery.length >= 2) {
      this.onSearch();
    } else if (this.searchQuery.length === 0) {
      this.loadDefaultUsers();
    }
  }
}
