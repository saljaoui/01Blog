import { Component } from '@angular/core';
import { Header } from '../../shared/components/header/header';
import { ProfileCard } from '../../shared/components/profile-card/profile-card';

@Component({
  selector: 'app-notifications',
  imports: [ Header, ProfileCard ],
  templateUrl: './notifications.html',
  styleUrl: './notifications.scss'
})
export class Notifications {

}
