import { CommonModule } from '@angular/common';
import { Component, Input } from '@angular/core';

@Component({
  selector: 'app-popup',
  imports: [CommonModule],
  templateUrl: './popup.html',
  styleUrl: './popup.scss',
})
export class Popup {
  message = '';
  type: 'success' | 'error' = 'success';
  visible = false;

  show(message: string, type: boolean) {
    this.message = message;
    this.type = type ? 'success' : 'error';
    this.visible = true;

    setTimeout(() => this.visible = false, 3000);
  }
}
