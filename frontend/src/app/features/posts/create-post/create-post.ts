import { CommonModule } from '@angular/common';
import { Component, OnInit, OnDestroy, ViewChild } from '@angular/core';
import { FormsModule, ReactiveFormsModule, FormBuilder, FormGroup } from '@angular/forms';
import { ActivatedRoute, Router, RouterLink } from '@angular/router';
import EditorJS from '@editorjs/editorjs';
import Header from '@editorjs/header';
import Paragraph from '@editorjs/paragraph';
import Delimiter from '@editorjs/delimiter';
import ImageTool from '@editorjs/image';
import VideoTool from 'editorjs-video';
import { HttpClient } from '@angular/common/http';
import { AuthService } from '../../../core/services/auth.service';
import { PostService } from '../../../core/services/post.service';
import { parseEditorJsContent } from '../../../core/utils/editorjs-parser';
import { Popup } from '../../../components/popup/popup';

@Component({
  selector: 'app-create-post',
  standalone: true,
  imports: [CommonModule, FormsModule, ReactiveFormsModule, RouterLink, Popup],
  templateUrl: './create-post.html',
  styleUrls: ['./create-post.scss']
})
export class CreatePost implements OnInit, OnDestroy {
  editor?: EditorJS;
  @ViewChild('popup') popup!: Popup;

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
      title: '',
      content: ['']
    });
  }

  ngOnInit(): void {
    this.postId = this.route.snapshot.paramMap.get('id');

    if (this.postId) {
      // Edit mode: load existing post
      this.postService.getPostById(this.postId).subscribe(async (post: any) => {
        // this.title = post.title;
        console.log("this.title >>>>>>>>", post);

        this.form.patchValue({
          title: post.title,
          content: post.content
        });

        if (this.editor && post.content) {
          const parsed = parseEditorJsContent(post.content);
          console.log("parsed", parsed.blocks);
          if (post.content) {
            try {
              // Wait for editor to be ready before rendering
              await this.editor.isReady;

              const parsed = parseEditorJsContent(post.content);
              console.log("parsed", parsed.blocks);

              this.title = post.title;
              this.editor.render({
                time: Date.now(),
                blocks: parsed.blocks,
                version: '2.28.0'
              });

              console.log('Content rendered!');
            } catch (error) {
              console.error('Render error', error);
            }
          }
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
      title: this.title,
      content: JSON.stringify(output.blocks)
    };

    console.log('postData !', postData);

    this.postService.createPost(postData).subscribe({
      next: res => {
        console.log('Post created!', res);
        this.popup.show('Post created successfully.', true);
        this.router.navigate(['/home']);
      },
      error: err => this.popup.show(err.error.message, false)
    });

  }

  async updatePost() {
        if (!this.editor) return;

    const output = await this.editor.save();
    const postData = {
      title: this.title,
      content: JSON.stringify(output.blocks)
    };

    if (this.postId) {
      this.postService.updatePost(this.postId, postData).subscribe({
        next: res => {
          console.log('Post updated!', res);
          this.router.navigate(['/posts', this.postId]);
        },
        error: err => console.error('Update error:', err)
      });
    }
  }

  ngOnDestroy(): void {
    this.editor?.destroy();
  }
}
