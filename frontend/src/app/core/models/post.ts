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
  saved?: boolean;
  savesCount?: number;
  content?: string;
  parsedContent?: any;
  title?: string;
}
