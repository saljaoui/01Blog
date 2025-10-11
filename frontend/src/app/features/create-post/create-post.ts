import { CommonModule } from '@angular/common';
import { Component, signal } from '@angular/core';
import { FormsModule } from '@angular/forms';
import { Router } from '@angular/router';
import { QuillModule } from 'ngx-quill';

@Component({
  selector: 'app-create-post',
  imports: [CommonModule, FormsModule, QuillModule],
  templateUrl: './create-post.html',
  styleUrl: './create-post.scss'
})
export class CreatePost {
   title = signal('');
  category = signal('');
  content = signal('');

  constructor(private router: Router) {}

  autoResize(event: any) {
    const textarea = event.target;
    textarea.style.height = 'auto';
    textarea.style.height = textarea.scrollHeight + 'px';
  }

  formatText(command: string) {
    document.execCommand(command, false);
  }

  addLink() {
    const url = prompt('Enter URL:');
    if (url) {
      document.execCommand('createLink', false, url);
    }
  }

  triggerImageUpload() {
    const fileInput = document.querySelector('input[type="file"]') as HTMLInputElement;
    fileInput?.click();
  }

  handleImageUpload(event: any) {
    const file = event.target.files[0];
    if (file) {
      const reader = new FileReader();
      reader.onload = (e: any) => {
        const img = document.createElement('img');
        img.src = e.target.result;
        img.style.maxWidth = '100%';
        img.style.borderRadius = '8px';
        img.style.margin = '1.5rem 0';
        
        const editor = document.querySelector('.content-editor');
        editor?.appendChild(img);
      };
      reader.readAsDataURL(file);
    }
  }

  onContentChange(event: any) {
    this.content.set(event.target.innerHTML);
  }

  saveDraft() {
    if (!this.title().trim()) {
      alert('Please add a title');
      return;
    }

    const draft = {
      title: this.title(),
      category: this.category(),
      content: this.content(),
      savedAt: new Date().toISOString()
    };

    localStorage.setItem('postDraft', JSON.stringify(draft));
    alert('✅ Draft saved!');
    console.log('Draft:', draft);
  }

  publishPost() {
    if (!this.title().trim()) {
      alert('Please add a title');
      return;
    }

    if (!this.category()) {
      alert('Please select a category');
      return;
    }

    if (this.content().length < 50) {
      alert('Please write at least 50 characters');
      return;
    }

    const post = {
      title: this.title(),
      category: this.category(),
      content: this.content(),
      publishedAt: new Date().toISOString()
    };

    console.log('Publishing:', post);
    alert('🎉 Post published!');
    
    // Send to your API:
    // this.postService.createPost(post).subscribe(...)
    
    localStorage.removeItem('postDraft');
    this.router.navigate(['/']);
  }

  goBack() {
    this.router.navigate(['/']);
  }
}
