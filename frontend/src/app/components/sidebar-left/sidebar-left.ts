import { Component } from '@angular/core';
import { ProfileCard } from '../profile-card/profile-card';
import { RouterLink, RouterLinkActive } from '@angular/router';

@Component({
  selector: 'app-sidebar-left',
  imports: [ProfileCard, RouterLink, RouterLinkActive],
  templateUrl: './sidebar-left.html',
  styleUrl: './sidebar-left.scss'
})
export class SidebarLeft {

}
