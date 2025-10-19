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
  posts: any[] = []; // store posts with parsed content

  constructor(private postService: PostService) {}

  ngOnInit(): void {
    this.postService.getAllPosts().subscribe({
      next: (res: any[]) => {
        this.posts = res.map(post => ({
          ...post,
          parsedContent: parseEditorJsContent(post.content)
        }));

        console.log('Posts fetched successfully', this.posts);
      },
      error: (err) => {
        console.error('Failed to fetch posts', err);
      }
    });
  }
}