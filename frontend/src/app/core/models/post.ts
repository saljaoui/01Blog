export interface Post {
  id: string;
  
  title?: string;
  content?: string;
  parsedContent?: any;
  
  authorName?: string;
  authorFirstName?: string;
  authorLastName?: string;
  authorAvatar?: string;
  authorImage?: string;
  
  createdAt?: string;
  
  likesCount?: number;
  commentsCount?: number;
  savesCount?: number;
  
  liked?: boolean;
  saved?: boolean;
}