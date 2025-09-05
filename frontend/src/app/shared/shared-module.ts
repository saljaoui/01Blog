

import { NgModule } from '@angular/core';
import { CommonModule } from '@angular/common';
import { Header } from './components/header/header';
import { PostCard } from './components/post-card/post-card';
import { ProfileCard } from './components/profile-card/profile-card';
import { CreatePostSection } from './components/create-post-section/create-post-section';

@NgModule({
  declarations: [],
  imports: [
    CommonModule,
    Header,
    PostCard,
    ProfileCard,
    CreatePostSection
  ],
  exports: [
    Header,
    PostCard,
    ProfileCard,
    CreatePostSection
  ]
})

export class SharedModule {}
