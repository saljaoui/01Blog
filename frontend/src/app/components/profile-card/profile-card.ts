import { Component, inject, NgModule, OnInit } from '@angular/core';
import { UserService } from '../../core/services/user.service';
import { User } from '../../core/models/user';
import { CommonModule } from '@angular/common';

@Component({
  selector: 'app-profile-card',
  imports: [CommonModule],
  templateUrl: './profile-card.html',
  styleUrl: './profile-card.scss'
})
export class ProfileCard implements OnInit {
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
