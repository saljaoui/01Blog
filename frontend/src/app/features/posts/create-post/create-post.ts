import { Component, ElementRef, ViewChild } from '@angular/core';
import { FormsModule } from '@angular/forms';
import { CommonModule } from '@angular/common';
import { Header } from '../../../shared/components/header/header';

interface ContentBlock {
  type: 'heading' | 'text' | 'image';
  content: string;
  level?: number; // for headings (h1, h2, h3, etc.)
}

@Component({
  selector: 'app-create-post',
  imports: [ Header, FormsModule, CommonModule ], // Add both FormsModule and CommonModule
  templateUrl: './create-post.html',
  styleUrl: './create-post.scss'
})
export class CreatePost {
  @ViewChild('imageUpload') imageUpload!: ElementRef;
  
  blogTitle: string = '';
  mainContent: string = '';
  contentBlocks: ContentBlock[] = [];

  addTextBlock() {
    this.contentBlocks.push({
      type: 'text',
      content: ''
    });
  }
  addHeadingBlock() {
  this.contentBlocks.push({
    type: 'heading',
    content: '',
    level: 2 // default to h2
  });
}

  triggerImageUpload() {
    this.imageUpload.nativeElement.click();
  }

  handleImageUpload(event: any) {
    const file = event.target.files[0];
    if (file) {
      const reader = new FileReader();
      reader.onload = (e: any) => {
        this.contentBlocks.push({
          type: 'image',
          content: e.target.result
        });
      };
      reader.readAsDataURL(file);
      event.target.value = '';
    }
  }

  removeBlock(index: number) {
    this.contentBlocks.splice(index, 1);
  }

  createPost() {
    const postData = {
      title: this.blogTitle,
      contentBlocks: this.contentBlocks,
      mainContent: this.mainContent
    };
    console.log('Creating post:', postData);
  }

  cancelPost() {
    this.blogTitle = '';
    this.mainContent = '';
    this.contentBlocks = [];
  }
}