import { Component } from '@angular/core';
import { ProfileCard } from '../profile-card/profile-card';

@Component({
  selector: 'app-sidebar-left',
  imports: [ProfileCard],
  templateUrl: './sidebar-left.html',
  styleUrl: './sidebar-left.scss'
})
export class SidebarLeft {

}
