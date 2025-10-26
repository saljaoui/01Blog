import { Component, inject, Input, OnInit } from '@angular/core';
import { UserService } from '../../core/services/user.service';
import { User } from '../../core/models/user';
import { SidebarRight } from '../../components/sidebar-right/sidebar-right';
import { CommonModule } from '@angular/common';
import { ActivatedRoute, RouterLinkActive, Router } from '@angular/router';
import { FormsModule } from '@angular/forms';

@Component({
  selector: 'app-profile',
  imports: [SidebarRight, CommonModule, FormsModule],
  templateUrl: './profile.html',
  styleUrl: './profile.scss'
})

export class Profile implements OnInit {
  private userService = inject(UserService);
  private route = inject(ActivatedRoute);
  private router = inject(Router);

  user?: User;
  currentUsername?: string;
  isFollowing: boolean = false;
  isLoading: boolean = false;
  showEditPopup: boolean = false;

  ngOnInit(): void {
    this.currentUsername = this.route.snapshot.paramMap.get('username') || '';

    if (this.currentUsername) {
      this.loadUserProfile();
    }
  }

  ngOnChanges(): void {
    if (this.user && !this.user.currentUser) {
      this.checkFollowStatus();
    }
  }

  loadUserProfile(): void {
    if (!this.currentUsername) return;

    this.userService.getUserByUsername(this.currentUsername).subscribe({
      next: (user) => {
        this.user = user;
        if (!user.currentUser) {
          this.checkFollowStatus();
        }
      },
      error: (err) => {
        console.error('Error fetching user:', err);
      }
    });
  }

  checkFollowStatus(): void {
    if (!this.user) return;
    this.userService.isFollowing(this.user.id).subscribe({
      next: (isFollowing) => {
        this.isFollowing = isFollowing;
      },
      error: (err) => {
        console.error('Error checking follow status:', err);
      }
    });
  }

  onFollowClick(): void {
    if (!this.user || this.isLoading) return;

    this.isLoading = true;
    const action = this.isFollowing ? this.userService.unfollow(this.user.id) : this.userService.follow(this.user.id);

    action.subscribe({
      next: () => {
        this.isFollowing = !this.isFollowing;
        // Update follower count
        if (this.isFollowing) {
          this.user!.followersCount++;
        } else {
          this.user!.followersCount--;
        }
        this.isLoading = false;
      },
      error: (err) => {
        console.error('Error following/unfollowing user:', err);
        this.isLoading = false;
      }
    });
  }


  onEditProfile() {
    this.showEditPopup = true;
  }

  closeEditPopup() {
    this.showEditPopup = false;
  }

  submitEditProfile(): void {
    if (!this.user) return;
    console.log('Profile updated', this.user);
    this.closeEditPopup();
  }

}
