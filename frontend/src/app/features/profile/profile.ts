import { Component, inject, Input, OnInit } from '@angular/core';
import { UserService } from '../../core/services/user.service';
import { User } from '../../core/models/user';
import { SidebarRight } from '../../components/sidebar-right/sidebar-right';
import { CommonModule } from '@angular/common';
import { ActivatedRoute, RouterLinkActive } from '@angular/router';

@Component({
  selector: 'app-profile',
  imports: [SidebarRight, CommonModule],
  templateUrl: './profile.html',
  styleUrl: './profile.scss'
})

export class Profile implements OnInit {
  private userService = inject(UserService);
  private route = inject(ActivatedRoute);
  
  user?: User;
  currentUsername?: string;
  isFollowing: boolean = false;
  isLoading: boolean = false;

  ngOnInit(): void {
    this.currentUsername = this.route.snapshot.paramMap.get('username') || '';
    
    if (this.currentUsername) {
      this.loadUserProfile();
    }
  }

  loadUserProfile(): void {
    if (!this.currentUsername) return;
    
    this.userService.getUserByUsername(this.currentUsername).subscribe({
      next: (user) => {
        this.user = user;
      },
      error: (err) => {
        console.error('Error fetching user:', err);
      }
    });
  }

}
