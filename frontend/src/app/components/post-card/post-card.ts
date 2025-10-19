import { Component, Input, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';

@Component({
  selector: 'app-post-card',
  standalone: true,
  imports: [CommonModule],
  templateUrl: './post-card.html',
  styleUrls: ['./post-card.scss']
})
export class PostCard implements OnInit {
  @Input() post: any;

  authorName: string = '';
  authorAvatar?: string | null;
  excerpt: string = '';
  imageUrl?: string | null;
  createdAt?: string | null;

  ngOnInit(): void {
    // safe guards
    if (!this.post) return;

    // author
    const first = this.post.authorFirstName ?? '';
    const last = this.post.authorLastName ?? '';
    this.authorName = `${first} ${last}`.trim() || (this.post.authorName ?? ''); 
    this.authorAvatar = this.post.authorAvatar ?? this.post.authorImage ?? null;

    // createdAt
    this.createdAt = this.post.createdAt ?? null;

    // get blocks: support both array and { blocks: [...] } formats
    let blocks: any[] = [];
    if (this.post.parsedContent?.blocks && Array.isArray(this.post.parsedContent.blocks)) {
      blocks = this.post.parsedContent.blocks;
    } else {
      try {
        const parsed = JSON.parse(this.post.content);
        blocks = Array.isArray(parsed) ? parsed : parsed?.blocks ?? [];
      } catch (e) {
        blocks = [];
      }
    }

    // first image (check common paths)
    const imageBlock = blocks.find(b => b.type === 'image');
    this.imageUrl = imageBlock?.data?.file?.url ?? imageBlock?.data?.url ?? null;

    // first paragraph as excerpt (strip html tags)
    const paragraphBlock = blocks.find(b => b.type === 'paragraph' && b.data?.text);
    if (paragraphBlock) {
      this.excerpt = this.stripHtml(paragraphBlock.data.text).trim();
    } else {
      // fallback: try header or use title
      const headerBlock = blocks.find(b => b.type === 'header' && b.data?.text);
      this.excerpt = headerBlock ? this.stripHtml(headerBlock.data.text).trim() : (this.post.title ?? '');
    }

    // limit excerpt length for card (won't change style)
    if (this.excerpt.length > 200) {
      this.excerpt = this.excerpt.slice(0, 197).trim() + '...';
    }
  }

  private stripHtml(s: string): string {
    return String(s).replace(/<[^>]*>/g, '');
  }
}
