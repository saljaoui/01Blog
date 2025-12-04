import { Component, OnInit } from '@angular/core';
import { SidebarRight } from '../../../components/sidebar-right/sidebar-right';
import { PostService } from '../../../core/services/post.service';
import { PostCard } from '../../../components/post-card/post-card';
import { parseEditorJsContent } from '../../../core/utils/editorjs-parser';
import { CommonModule } from '@angular/common';

@Component({
  selector: 'app-home',
  imports: [CommonModule, SidebarRight, PostCard],
  templateUrl: './home.html',
  styleUrl: './home.scss'
})
export class Home implements OnInit {
  // Properties
  posts: any[] = [];
  currentPage: number = 0;
  pageSize: number = 5;
  isLoading: boolean = false;
  hasMorePosts: boolean = true;
  currentFilter: 'all' | 'followed' = 'all';

  // Dependency Injection
  constructor(private postService: PostService) {}

  // ===== LIFECYCLE HOOKS =====
  ngOnInit(): void {
    this.loadPosts();
  }

  // ===== DATA LOADING =====
  loadPosts(): void {
    if (this.isLoading || !this.hasMorePosts) return;

    this.isLoading = true;
    const serviceMethod = this.currentFilter === 'followed' 
      ? this.postService.getFollowedPosts(this.currentPage, this.pageSize) 
      : this.postService.getAllPosts(this.currentPage, this.pageSize);
      
    serviceMethod.subscribe({
      next: (res: any[]) => {
        if (res.length === 0) {
          this.hasMorePosts = false;
        } else {
          const newPosts = res.map(post => ({
            ...post,
            parsedContent: parseEditorJsContent(post.content)
          }));
          this.posts = [...this.posts, ...newPosts];
          this.currentPage++;
        }
        this.isLoading = false;
      },
      error: (err) => {
        console.error('Failed to fetch posts', err);
        this.isLoading = false;
      }
    });
  }

  // ===== PAGINATION ACTIONS =====
  loadMorePosts(): void {
    this.loadPosts();
  }

  // ===== FILTER ACTIONS =====
  switchFilter(filter: 'all' | 'followed'): void {
    if (this.currentFilter === filter) return;
    this.currentFilter = filter;
    this.posts = [];
    this.currentPage = 0;
    this.hasMorePosts = true;
    this.loadPosts();
  }
}
