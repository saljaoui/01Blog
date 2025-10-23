import { Component, inject, OnInit } from '@angular/core';
import { SidebarRight } from '../../components/sidebar-right/sidebar-right';
import { CommonModule } from '@angular/common';
import { PostCard } from '../../components/post-card/post-card';
import { SaveService } from '../../core/services/save.service';
import { Post } from '../../core/models/post';

@Component({
  selector: 'app-saved-posts',
  imports: [SidebarRight, CommonModule, PostCard],
  templateUrl: './saved-posts.html',
  styleUrl: './saved-posts.scss'
})
export class SavedPosts implements OnInit {
  private saveService = inject(SaveService);

  savedPosts: Post[] = [];
  isLoading: boolean = false;

  ngOnInit(): void {
    this.loadSavedPosts();
  }

  loadSavedPosts(): void {
    this.isLoading = true;
    this.saveService.getSavedPosts().subscribe({
      next: (posts) => {
        this.savedPosts = posts;
        this.isLoading = false;
      },
      error: (err) => {
        console.error('Error loading saved posts:', err);
        this.isLoading = false;
      }
    });
  }
}
