import { Component } from '@angular/core';
import { Router, RouterLink } from '@angular/router';
import { UserLogin } from '../../../core/models/user';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { AuthService } from '../../../core/services/auth.service';

@Component({
  selector: 'app-login',
  imports: [RouterLink, CommonModule, FormsModule],
  templateUrl: './login.html',
  styleUrl: '../../../../styles/auth.scss'
})

export class Login {
  passwordVisible: boolean = false;
  isSubmitting = false;
  userLogin: UserLogin = {
    username: "",
    password: ""
  }

  constructor(private authService: AuthService, private router: Router) { }

  onSubmit() {
    this.authService.login(this.userLogin).subscribe({
      next: (res: any) => {
        this.isSubmitting = true;
        console.log('Login successful', res);

        setTimeout(() => {
          this.router.navigate(['/home']);
        }, 300);

      },
      error: (err) => {
        console.error('Login failed', err);
        this.isSubmitting = false;
      }
    })
  }

  togglePassword() {
    this.passwordVisible = !this.passwordVisible;
  }
}
