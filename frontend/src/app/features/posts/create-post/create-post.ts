import { CommonModule } from '@angular/common';
import { Component } from '@angular/core';
import { FormsModule, ReactiveFormsModule } from '@angular/forms';
import EditorJS from '@editorjs/editorjs';
import Header from '@editorjs/header';
import Paragraph from '@editorjs/paragraph';
import ImageTool from '@editorjs/image';
import VideoTool from 'editorjs-video';
import { HttpClient } from '@angular/common/http';
import { AuthService } from '../../../core/services/auth.service';

@Component({
  selector: 'app-create-post',
  imports: [CommonModule, FormsModule, ReactiveFormsModule],
  templateUrl: './create-post.html',
  styleUrl: './create-post.scss'
})

export class CreatePost {
  editor?: EditorJS;
  title: string = '';

  constructor(private authService: AuthService, private http: HttpClient) { }

  ngOnInit(): void {
    console.log("this.authService.getAccessToken :", this.authService.getAccessToken());

    this.editor = new EditorJS({
      holder: 'editorjs',
      placeholder: 'Start writing your post...',
      tools: {
        header: Header,
        paragraph: Paragraph,
        image: {
          class: ImageTool,
          config: {
            uploader: {
              uploadByFile: (file: File) => {
                const formData = new FormData();
                formData.append('image', file);

                return this.http.post<any>('http://localhost:8080/api/posts/upload-image', formData,
                  {
                    headers: {
                      Authorization: `Bearer ${this.authService.getAccessToken()}`,
                    }
                  }
                )
                  .toPromise()
                  .then(response => ({
                    success: 1,
                    file: { url: response.file.url }
                  }))
                  .catch(() => ({
                    success: 0
                  }));
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
                  {
                    headers: {
                      Authorization: `Bearer ${this.authService.getAccessToken()}`,
                    },
                  }
                )
                  .toPromise()
                  .then((response) => ({
                    success: 1,
                    file: { url: response.file.url }, // backend must return { file: { url: '...' } }
                  }))
                  .catch(() => ({
                    success: 0,
                  }));
              },
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
      content: JSON.stringify(output.blocks),
    };

    console.log('Post content:', postData);

    // Send to backend
    this.http.post('http://localhost:8080/api/posts', postData).subscribe({
      next: (response) => console.log('Saved!', response),
      error: (error) => console.error('Error:', error)
    });
  }

  ngOnDestroy(): void {
    this.editor?.destroy();
  }
}
