import { CommonModule } from '@angular/common';
import { Component, OnInit, OnDestroy } from '@angular/core';
import { FormsModule, ReactiveFormsModule, FormBuilder, FormGroup } from '@angular/forms';
import { ActivatedRoute, Router } from '@angular/router';
import EditorJS from '@editorjs/editorjs';
import Header from '@editorjs/header';
import Paragraph from '@editorjs/paragraph';
import Delimiter from '@editorjs/delimiter';
import ImageTool from '@editorjs/image';
import VideoTool from 'editorjs-video';
import { HttpClient } from '@angular/common/http';
import { AuthService } from '../../../core/services/auth.service';
import { PostService } from '../../../core/services/post.service';

@Component({
  selector: 'app-create-post',
  standalone: true,
  imports: [CommonModule, FormsModule, ReactiveFormsModule],
  templateUrl: './create-post.html',
  styleUrls: ['./create-post.scss']
})
export class CreatePost implements OnInit, OnDestroy {
  editor?: EditorJS;
  form: FormGroup;
  postId: string | null = null;
  title: string = '';

  constructor(
    private authService: AuthService,
    private postService: PostService,
    private http: HttpClient,
    private fb: FormBuilder,
    private route: ActivatedRoute,
    private router: Router
  ) {
    this.form = this.fb.group({
      title: [''],
      content: ['']
    });
  }

  ngOnInit(): void {
    this.postId = this.route.snapshot.paramMap.get('id');

    if (this.postId) {
      // Edit mode: load existing post
      this.postService.getPostById(this.postId).subscribe((post: any) => {
        this.title = post.title;
        this.form.patchValue({
          title: post.title,
          content: post.content
        });
        // Load content into EditorJS if needed
        if (this.editor && post.content) {
          const blocks = JSON.parse(post.content);
          this.editor.render({ blocks });
        }
      });
    }

    this.editor = new EditorJS({
      holder: 'editorjs',
      placeholder: 'Start writing your post...',
      tools: {
        header: Header,
        paragraph: Paragraph,
        delimiter: Delimiter,
        image: {
          class: ImageTool,
          config: {
            uploader: {
              uploadByFile: (file: File) => {
                const formData = new FormData();
                formData.append('image', file);
                return this.http.post<any>(
                  'http://localhost:8080/api/posts/upload-image',
                  formData,
                  { headers: { Authorization: `Bearer ${this.authService.getAccessToken()}` } }
                ).toPromise()
                  .then(res => ({ success: 1, file: { url: res.file.url } }))
                  .catch(() => ({ success: 0 }));
              }
            }
          }
        },
        video: {
          class: VideoTool,
          config: {
            uploader: {
              uploadByFile: (file: File) => {
                const formData = new FormData();
                formData.append('video', file);
                return this.http.post<any>(
                  'http://localhost:8080/api/posts/upload-video',
                  formData,
                  { headers: { Authorization: `Bearer ${this.authService.getAccessToken()}` } }
                ).toPromise()
                  .then(res => ({ success: 1, file: { url: res.file.url } }))
                  .catch(() => ({ success: 0 }));
              }
            }
          }
        }
      }
    });
  }

  async savePost() {
    if (!this.editor) return;

    const output = await this.editor.save();
    const postData = {
      title: this.form.value.title,
      content: JSON.stringify(output.blocks)
    };

    if (this.postId) {
      // Update existing post
      this.postService.updatePost(this.postId, postData).subscribe({
        next: res => {
          console.log('Post updated!', res);
          this.router.navigate(['/posts', this.postId]);
        },
        error: err => console.error('Update error:', err)
      });
    } else {
      // Create new post
      this.postService.createPost(postData).subscribe({
        next: res => {
          console.log('Post created!', res);
          this.router.navigate(['/posts', res.id]);
        },
        error: err => console.error('Create error:', err)
      });
    }
  }

  ngOnDestroy(): void {
    this.editor?.destroy();
  }
}
