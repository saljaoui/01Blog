export interface Post {
  id: string;
  authorFirstName?: string;
  authorLastName?: string;
  authorName?: string;
  authorAvatar?: string;
  authorImage?: string;
  createdAt?: string;
  liked?: boolean;
  likesCount?: number;
  content?: string;
  parsedContent?: any;
  title?: string;
}
