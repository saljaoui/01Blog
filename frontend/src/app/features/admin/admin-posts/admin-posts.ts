import { Component, inject, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { PostService } from '../../../core/services/post.service';
import { Post } from '../../../core/models/post';
import { RouterLink } from '@angular/router';

@Component({
  selector: 'app-admin-posts',
  imports: [CommonModule, RouterLink],
  templateUrl: './admin-posts.html',
  styleUrl: './admin-posts.scss'
})
export class AdminPosts implements OnInit {
  private postService = inject(PostService);
  posts: Post[] = [];
  filteredPosts: Post[] = [];
  displayedPosts: Post[] = [];
  searchTerm = '';
  displayedCount = 10;

  ngOnInit() {
    this.loadPosts();
  }

  loadPosts() {
    this.postService.getAllPosts().subscribe({
      next: (posts) => {
        console.log("Posts loaded:", posts);
        this.posts = posts;
        this.filteredPosts = posts;
        this.displayedPosts = this.filteredPosts.slice(0, this.displayedCount);
      },
      error: (error) => {
        console.error('Error loading posts:', error);
      }
    });
  }

  onSearch(event: Event) {
    const target = event.target as HTMLInputElement;
    this.searchTerm = target.value.toLowerCase();
    this.applyFilters();
  }

  applyFilters() {
    this.filteredPosts = this.posts.filter(post => {
      const matchesSearch = post.authorUsername?.toLowerCase().includes(this.searchTerm) ||
        post.authorName?.toLowerCase().includes(this.searchTerm);
      return matchesSearch;
    });
    this.displayedCount = 10;
    this.displayedPosts = this.filteredPosts.slice(0, this.displayedCount);
  }

  loadMore() {
    this.displayedCount += 10;
    this.displayedPosts = this.filteredPosts.slice(0, this.displayedCount);
  }

  formatDate(dateString: string): string {
    const date = new Date(dateString);
    return date.toLocaleDateString('en-US', { month: 'short', day: 'numeric', year: 'numeric' });
  }

  viewPost(post: Post) {
    console.log('View post:', post.id);
  }

  deletePost(post: Post) {
    console.log('Delete post:', post.id);
  }
}
