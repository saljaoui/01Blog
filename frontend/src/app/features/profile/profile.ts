import { Component, inject, Input, OnInit } from '@angular/core';
import { UserService } from '../../core/services/user.service';
import { User } from '../../core/models/user';
import { SidebarRight } from '../../components/sidebar-right/sidebar-right';
import { ReportUserPopup } from '../../components/report-user-popup/report-user-popup';
import { CommonModule } from '@angular/common';
import { ActivatedRoute, RouterLinkActive, Router } from '@angular/router';
import { FormsModule } from '@angular/forms';
import { ReportService } from '../../core/services/report.service';
import { PostService } from '../../core/services/post.service';
import { PostCard } from '../../components/post-card/post-card';
import { parseEditorJsContent } from '../../core/utils/editorjs-parser';

@Component({
  selector: 'app-profile',
  imports: [SidebarRight, ReportUserPopup, CommonModule, FormsModule, PostCard],
  templateUrl: './profile.html',
  styleUrl: './profile.scss'
})

export class Profile implements OnInit {
  private userService = inject(UserService);
  private route = inject(ActivatedRoute);
  private reportService = inject(ReportService);
  private postService = inject(PostService);
  private router = inject(Router);

  user?: User;
  currentUsername?: string;
  isFollowing: boolean = false;
  isLoading: boolean = false;
  showEditPopup: boolean = false;
  showMenu: boolean = false;
  showReportPopup: boolean = false;
  posts: any[] = [];
  editForm = {
    firstName: '',
    lastName: '',
    bio: '',
    avatar: null as File | null,
    avatarPreview: ''
  };
  reportForm = {
    reason: ''
  };

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
        this.loadUserPosts();
      },
      error: (err) => {
        console.error('Error fetching user:', err);
      }
    });
  }

  loadUserPosts(): void {
    if (!this.user) return;

    this.postService.getPostsByUser(this.user.id).subscribe({
      next: (posts: any[]) => {
        this.posts = posts.map(post => ({
          ...post,
          parsedContent: parseEditorJsContent(post.content)
        }));
      },
      error: (err) => {
        console.error('Error fetching user posts:', err);
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
    if (this.user) {
      this.editForm = {
        firstName: this.user.firstName,
        lastName: this.user.lastName,
        bio: this.user.bio || '',
        avatar: null,
        avatarPreview: this.user.avatarUrl || ''
      };
    }
    this.showEditPopup = true;
  }

  onMenuKlick() {
    this.showMenu = !this.showMenu;
  }

  closeEditPopup() {
    this.showEditPopup = false;
    this.editForm.avatar = null;
    this.editForm.avatarPreview = '';
  }

  onAvatarChange(event: any) {
    const file = event.target.files[0];
    if (file) {
      this.editForm.avatar = file;
      const reader = new FileReader();
      reader.onload = (e) => {
        this.editForm.avatarPreview = e.target?.result as string;
      };
      reader.readAsDataURL(file);
    }
  }

  submitEditProfile(): void {
    if (!this.user) return;

    console.log("this.editForm", this.editForm);

    const formData = new FormData();

    // Always add profile data
    formData.append('firstName', this.editForm.firstName);
    formData.append('lastName', this.editForm.lastName);
    formData.append('bio', this.editForm.bio);

    // Add avatar if present
    if (this.editForm.avatar) {
      formData.append('avatar', this.editForm.avatar);
    }

    this.userService.updateProfile(formData).subscribe({
      next: (updatedUser: User) => {
        this.user = updatedUser;
        this.closeEditPopup();
      },
      error: (err: any) => {
        console.error('Error updating profile:', err);
      }
    });
  }

  onShareClick() {
    const link = window.location.href;

    navigator.clipboard.writeText(link).then(() => {
      alert('✅ Link copied to clipboard!');
      this.showMenu = false;
    }).catch(err => {
      console.error('Failed to copy link:', err);
    });
  }

  onReportClick() {
    this.showReportPopup = true;
    this.showMenu = false;
  }

  closeReportPopup() {
    this.showReportPopup = false;
  }

  submitReportUser() {
    if (!this.user || !this.reportForm.reason.trim()) return;

    console.log("Report submitted");

    this.reportService.reportUser(this.user.id, this.reportForm.reason).subscribe({
      next: () => {
        alert('Report submitted successfully!');
        this.closeReportPopup();
        this.reportForm.reason = '';
      },
      error: (err) => {
        console.error('Error submitting report:', err);
        alert('Failed to submit report. Please try again.');
      }
    });
  }

}
