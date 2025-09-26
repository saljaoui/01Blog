import { Component } from '@angular/core';
import { AuthService } from '../../../core/services/auth.service';
import { Router } from '@angular/router';
import { User } from '../../../core/models/user';
import { FormsModule } from '@angular/forms';

@Component({
  selector: 'app-register',
  imports: [FormsModule],
  templateUrl: './register.html',
  styleUrl: '../../../../styles/auth.scss'
})
export class Register {

  passwordVisible: boolean = false;
  user: User = {
    username: "",
    email: "",
    password: ""
  }

  constructor(private authService: AuthService, private router: Router) { }

  onSubmit() {
    this.authService.register(this.user).subscribe({
      next: (res: any) => {
        console.log('Login successful', res);
        localStorage.setItem('token', res.token);
        // this.router.navigate(['/dashboard']);
      },
      error: (err) => {
        console.error('Login failed', err);
      }
    })
  }
  togglePassword() {
    this.passwordVisible = !this.passwordVisible;
  }
}
