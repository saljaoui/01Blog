import { Component, inject } from '@angular/core';
import { PostService } from '../../../core/services/post.service';
import { ActivatedRoute, Router, RouterLink } from '@angular/router';
import { Post } from '../../../core/models/post';
import { LikeService } from '../../../core/services/like.service';
import { SaveService } from '../../../core/services/save.service';
import { CommentService } from '../../../core/services/comment.service';
import { CommonModule } from '@angular/common';
import { DateUtilsService } from '../../../core/services/utils/DateUtil.service';
import { parseEditorJsContent } from '../../../core/utils/editorjs-parser';
import { Comment, CommentRequest, CommentLikeRequest } from '../../../core/models/comment';
import { FormsModule } from '@angular/forms';
import { ReportService } from '../../../core/services/report.service';
import { ReportUserPopup } from '../../../components/report-user-popup/report-user-popup';

@Component({
  selector: 'app-post-detail',
  imports: [CommonModule, FormsModule, ReportUserPopup, RouterLink],
  templateUrl: './post-detail.html',
  styleUrls: ['./post-detail.scss']
})
export class PostDetail {
  post!: any;
  createdAt?: string;
  comments: Comment[] = [];
  newCommentContent: string = '';
  showComments: boolean = false;
  showMenu: boolean = false;
  showReportPopup: boolean = false;
  reportForm = {
    reason: ''
  };

postService = inject(PostService);
likeService = inject(LikeService);
saveService = inject(SaveService);
commentService = inject(CommentService);
route = inject(ActivatedRoute);
router = inject(Router);
dateUtils = inject(DateUtilsService);
reportService = inject(ReportService)

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
    console.log("this.post", this.post);
    
  }

  onCommentClick() {
    if (!this.showComments) {
      this.loadComments();
    }
    this.showComments = !this.showComments;
  }

  closeReportPopup() {
    this.showReportPopup = false;
  }

  submitReportPost() {
    if (!this.post || !this.reportForm.reason.trim()) return;

    this.reportService.reportPost(this.post.id, this.reportForm.reason).subscribe({
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

  loadComments() {
    const postId = this.route.snapshot.paramMap.get('id');
    this.commentService.getCommentsByPostId(postId!).subscribe({
      next: (comments) => {
        this.comments = comments;
        // Load like status for each comment
        this.comments.forEach(comment => {
          this.commentService.getCommentLikeStatus(comment.id).subscribe({
            next: (likeStatus) => {
              comment.liked = likeStatus.liked;
              comment.likesCount = likeStatus.likesCount;
            },
            error: (err) => console.error('Error fetching comment like status', err)
          });
        });
      },
      error: (error) => {
        console.error('Error fetching comments:', error);
      }
    });
  }

  onSubmitComment() {
    if (!this.newCommentContent.trim()) return;

    const postId = this.route.snapshot.paramMap.get('id');
    const commentRequest: CommentRequest = { content: this.newCommentContent };

    this.commentService.createComment(postId!, commentRequest).subscribe({
      next: (newComment) => {
        this.comments.unshift(newComment); // Add to top
        this.newCommentContent = '';
        // Update post comment count
        if (this.post) {
          this.post.commentsCount = (this.post.commentsCount || 0) + 1;
        }
      },
      error: (error) => {
        console.error('Error creating comment:', error);
      }
    });
  }

  onLikeComment(comment: Comment) {
    const likeRequest: CommentLikeRequest = { commentId: comment.id };
    this.commentService.toggleCommentLike(likeRequest).subscribe({
      next: (res) => {
        comment.liked = res.liked;
        comment.likesCount = res.likesCount;
      },
      error: (err) => console.error('Comment like error', err)
    });
  }

  canDeleteComment(comment: Comment): boolean {
    // For now, allow deletion if the comment belongs to the current user
    // In a real app, you'd check the current user's ID against comment.authorId
    return true; // TODO: Implement proper user check
  }

  onDeleteComment(comment: Comment) {
    if (confirm('Are you sure you want to delete this comment?')) {
      this.commentService.deleteComment(comment.id).subscribe({
        next: () => {
          // Remove comment from the list
          this.comments = this.comments.filter(c => c.id !== comment.id);
          // Update post comment count
          if (this.post) {
            this.post.commentsCount = (this.post.commentsCount || 0) - 1;
          }
        },
        error: (err) => console.error('Comment delete error', err)
      });
    }
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

  toggleMenu() {
    this.showMenu = !this.showMenu;
  }

  onReportClick() {
    this.showReportPopup = true;
    this.showMenu = false;
  }

  onDelete() {
    if (confirm('Are you sure you want to delete this post?')) {
      const postId = this.route.snapshot.paramMap.get('id');
      this.postService.deletePost(postId!).subscribe({
        next: () => {
          this.router.navigate(['/dashboard']);
        },
        error: (err: any) => console.error('Delete post error', err)
      });
    }
    this.showMenu = false;
  }

  onEdit() {
    const postId = this.route.snapshot.paramMap.get('id');
    this.router.navigate(['/posts/edit', postId]);
    this.showMenu = false;
  }

}
