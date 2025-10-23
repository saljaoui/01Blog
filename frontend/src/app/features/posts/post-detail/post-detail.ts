import { Component } from '@angular/core';
import { PostService } from '../../../core/services/post.service';
import { ActivatedRoute } from '@angular/router';
import { Post } from '../../../core/models/post';
import { LikeService } from '../../../core/services/like.service';
import { SaveService } from '../../../core/services/save.service';
import { CommonModule } from '@angular/common';
import { DateUtilsService } from '../../../core/services/utils/DateUtil.service';
import { parseEditorJsContent } from '../../../core/utils/editorjs-parser';

@Component({
  selector: 'app-post-detail',
  imports: [CommonModule],
  templateUrl: './post-detail.html',
  styleUrls: ['./post-detail.scss']
})
export class PostDetail {
  post!: any;
  createdAt?: string;

  constructor(private postService: PostService,
    private likeService: LikeService,
    private saveService: SaveService,
    private route: ActivatedRoute,
    private dateUtils: DateUtilsService
  ) { }

  ngOnInit() {
    const postId = this.route.snapshot.paramMap.get('id');
    this.postService.getPostById(postId).subscribe({
      next: (post) => {
        this.post = {
          ...post,
          parsedContent: parseEditorJsContent(post.content)
        };
        this.createdAt = this.dateUtils.formatDate(post.createdAt);
        console.log('Post details:', this.post);
        
      },
      error: (error) => {
        console.error('Error fetching post details:', error);
      }
    });
  }

  onLike(): void {
    if (!this.post) return;
    this.likeService.toggleLike(this.post.id).subscribe({
      next: (res: any) => {
        if (this.post) {
          this.post.liked = res.liked;
          this.post.likesCount = res.likesCount;
        }
      },
      error: (err) => console.error('Like error', err)
    });
  }

  onSive(): void {
    if (!this.post) return;
    this.saveService.toggleSave(this.post.id).subscribe({
      next: (res: any) => {
        if (this.post) {
          this.post.saved = res.saved;
          this.post.savesCount = res.savesCount;
        }
      },
      error: (err) => console.error('Save error', err)
    });
  }

  private stripHtml(s: string): string {
    return String(s).replace(/<[^>]*>/g, '');
  }

}
