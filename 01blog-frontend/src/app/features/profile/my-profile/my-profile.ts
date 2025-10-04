import { Component } from '@angular/core';
import { SidebarLeft } from '../../../components/sidebar-left/sidebar-left';
import { SidebarRight } from '../../../components/sidebar-right/sidebar-right';

@Component({
  selector: 'app-my-profile',
  imports: [SidebarLeft, SidebarRight],
  templateUrl: './my-profile.html',
  styleUrl: './my-profile.scss'
})
export class MyProfile {

}
