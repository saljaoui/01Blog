import { Component } from '@angular/core';
import { AuthService } from '../../../core/services/auth.service';
import { Router, RouterLink } from '@angular/router';
import { FormsModule } from '@angular/forms';
import { UserRegister } from '../../../core/models/user';
import { CommonModule } from '@angular/common';

@Component({
  selector: 'app-register',
  imports: [FormsModule, CommonModule, RouterLink],
  templateUrl: './register.html',
  styleUrl: '../../../../styles/auth.scss'
})
export class Register {
  
  passwordVisible: boolean = false;
  isSubmitting = false;
  userRegister: UserRegister = {
    username: "",
    firstName: "",
    lastName: "",
    email: "",
    password: ""
  }

  constructor(private authService: AuthService, private router: Router) { }

  onSubmit() {
    this.authService.register(this.userRegister).subscribe({
      next: (res: any) => {
        this.isSubmitting = true;
        console.log('Login successful', res);
        localStorage.setItem('token', res.accessToken);
        // this.router.navigate(['/dashboard']);
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
