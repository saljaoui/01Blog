import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Router } from '@angular/router';
import { Observable, throwError } from 'rxjs';
import { tap, catchError } from 'rxjs/operators';
import { UserLogin, UserRegister } from '../models/user';

@Injectable({
  providedIn: 'root'
})
export class AuthService {
  private readonly apiUrl = 'http://localhost:8080/auth';

  constructor(private http: HttpClient, private router: Router) {}

  public register(userRegister: UserRegister): Observable<any> {
    return this.http.post(`${this.apiUrl}/register`, userRegister).pipe(
      tap((response: any) => {
        localStorage.setItem('access_token', response.accessToken);
        localStorage.setItem('refresh_token', response.refreshToken);
      }),
      catchError(error => throwError(() => error))
    );
  }

    public login(userLogin: UserLogin): Observable<any> {
    return this.http.post(`${this.apiUrl}/login`, userLogin).pipe(
      tap((response: any) => {
        localStorage.setItem('access_token', response.accessToken);
        localStorage.setItem('refresh_token', response.refreshToken);
      }),
      catchError(error => throwError(() => error))
    );
  }

  public refreshToken(): Observable<any> {
    const refreshToken = localStorage.getItem('refresh_token');
    return this.http.post<any>(`${this.apiUrl}/refresh`, { refreshToken }).pipe(
      tap((response: any) => {
        localStorage.setItem('access_token', response.accessToken);
        localStorage.setItem('refresh_token', response.refreshToken);
      })
    );
  }

  public logout(): void {
    localStorage.removeItem('access_token');
    localStorage.removeItem('refresh_token');
    this.router.navigate(['/login']);
  }

  public isAuthenticated(): boolean {
    return !!localStorage.getItem('access_token');
  }

  public getAccessToken(): string | null {
    return localStorage.getItem('access_token');
  }
}