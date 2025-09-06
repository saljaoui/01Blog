import { Component } from '@angular/core';
import { Header } from '../../../shared/components/header/header';
import { SidebarUser } from '../../../shared/components/sidebar-user/sidebar-user';
import { PostCard } from '../../../shared/components/post-card/post-card';

@Component({
  selector: 'app-user-profile',
  imports: [Header, SidebarUser, PostCard],
  templateUrl: './user-profile.html',
  styleUrl: './user-profile.scss'
})
export class UserProfile {

}
