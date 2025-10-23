import { Component, inject } from '@angular/core';
import { SidebarRight } from '../../../components/sidebar-right/sidebar-right';
import { User } from '../../../core/models/user';
import { UserService } from '../../../core/services/user.service';
import { CommonModule } from '@angular/common';

@Component({
  selector: 'app-my-profile',
  imports: [SidebarRight, CommonModule],
  templateUrl: './my-profile.html',
  styleUrl: './my-profile.scss'
})
export class MyProfile {
  private userService = inject(UserService);
  user?: User;
  ngOnInit(): void {
    this.userService.getCurrentUser().subscribe({
      next: (user) => {
        this.user = user;
      },
      error: (err) => {
        console.error('Error fetching user:', err);
      }
    });
  }
}
