import { Component } from '@angular/core';
import { SidebarRight } from '../../../components/sidebar-right/sidebar-right';
import { PostService } from '../../../core/services/post.service';

@Component({
  selector: 'app-home',
  imports: [SidebarRight],
  templateUrl: './home.html',
  styleUrl: './home.scss'
})

export class Home {
  constructor(private postService: PostService) { }
   ngOnInit(): void {
    this.postService.getAllPosts().subscribe({
      next: (res: any) => {
        console.log('Posts fetched successfully', res);
      },
      error: (err) => {
        console.error('Failed to fetch posts', err);
      }
    })
  }
}
