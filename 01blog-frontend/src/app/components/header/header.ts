import { Component, HostListener, signal } from '@angular/core';
import { RouterLink } from '@angular/router';

@Component({
  selector: 'app-header',
  imports: [RouterLink],
  templateUrl: './header.html',
  styleUrl: './header.scss'
})
export class Header {
  isOpen = signal(false);

 toggleDropdown() {
    this.isOpen.update((v: boolean) => !v);
    console.log(this.isOpen());
  }

  @HostListener('document:click', ['$event'])
  closeDropdown(event: MouseEvent) {
    const target = event.target as HTMLElement;
    if (!target.closest('.dropdown')) {
      this.isOpen.set(false);
    }
  }

  logout() {
    console.log('Logout clicked');
    this.isOpen.set(false);
    // Add your logout logic here
  }
}
