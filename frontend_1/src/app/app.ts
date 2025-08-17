import { Component, signal } from '@angular/core';
import { RouterLink, RouterOutlet } from '@angular/router';
import { LoginComponent } from './components/login/login.component';
import { HomeComponent } from './home/home.component';

@Component({
  selector: 'app-root',
  imports: [RouterOutlet, LoginComponent, RouterLink, HomeComponent],
  template: `
  <app-home />

  <router-outlet />
  `,
  styles: [],
})
export class App {
  protected readonly title = signal('frontend');
}
