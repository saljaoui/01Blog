import { Component, ViewChild } from '@angular/core';
import { Router, RouterLink } from '@angular/router';
import { UserLogin } from '../../../core/models/user';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { AuthService } from '../../../core/services/auth.service';
import { Popup } from '../../../components/popup/popup';

@Component({
  selector: 'app-login',
  imports: [RouterLink, CommonModule, FormsModule, Popup],
  templateUrl: './login.html',
  styleUrl: '../../../../styles/auth.scss'
})

export class Login {
  @ViewChild('popup') popup!: Popup;
  passwordVisible: boolean = false;
  isSubmitting = false;
  userLogin: UserLogin = {
    username: "",
    password: ""
  }

  constructor(private authService: AuthService, private router: Router) { }

  onSubmit() {
    console.log('click it');
    this.authService.login(this.userLogin).subscribe({
      next: (res: any) => {
        this.isSubmitting = true;
        console.log('Login successful', res);
        this.popup.show('Login successful.', true);
        setTimeout(() => {
          this.router.navigate(['/home']);
        }, 300);

      },
      error: (err) => {
        // console.error('Login failed', err);
        this.popup.show(err.error.message || 'Login failed. Please try again.', false);
        this.isSubmitting = false;
      }
    })
  }

  togglePassword() {
    this.passwordVisible = !this.passwordVisible;
  }
}
