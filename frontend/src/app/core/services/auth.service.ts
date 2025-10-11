import { Injectable } from '@angular/core';
import { Observable } from 'rxjs';
import {UserLogin, UserRegister } from '../models/user';
import { HttpClient } from '@angular/common/http';

@Injectable({
  providedIn: 'root'
})

export class AuthService {
  private apiUrl = 'http://localhost:8080/auth';

  constructor(private http: HttpClient) {}

  public register(userRegister: UserRegister): Observable<any> {
    return this.http.post(`${this.apiUrl}/register`, userRegister);
  }

  public login(userLogin: UserLogin): Observable<any> {
    return this.http.post(`${this.apiUrl}/login`, userLogin);
  }

  // nsfd post le backend bach n3rf wach refresh good wela la khas nzid n9lb hena
  public refreshToken() {
  const refreshToken = localStorage.getItem('refreshToken');
  return this.http.post(`${this.apiUrl}/refresh`, { refreshToken });
}

}
