import { Component, inject, OnInit } from '@angular/core';
import { UserService } from '../../core/services/user.service';
import { User } from '../../core/models/user';

@Component({
  selector: 'app-profile-card',
  imports: [],
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
        console.log('Current user:', user);
      },
      error: (err) => {
        console.error('Error fetching user:', err);
      }
    });
  }
}
