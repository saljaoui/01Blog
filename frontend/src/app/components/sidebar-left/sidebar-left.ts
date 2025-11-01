import { Component, inject, Input, HostBinding } from '@angular/core';
import { ProfileCard } from '../profile-card/profile-card';
import { RouterLink, RouterLinkActive } from '@angular/router';
import { User } from '../../core/models/user';
import { UserService } from '../../core/services/user.service';

@Component({
  selector: 'app-sidebar-left',
  standalone: true,
  imports: [ProfileCard, RouterLink, RouterLinkActive],
  templateUrl: './sidebar-left.html',
  styleUrl: './sidebar-left.scss'
})
export class SidebarLeft {
  private userService = inject(UserService);
  user?: User;

  @Input() isOpen = false;

  @HostBinding('class.open')
  get isOpenClass() {
    return this.isOpen;
  }

  ngOnInit(): void {
    this.userService.getCurrentUser().subscribe({
      next: (user) => {
        this.user = user;
        console.log("user ::::::", user);
        
      },
      error: (err) => {
        console.error('Error fetching user:', err);
      }
    });
  }
}
