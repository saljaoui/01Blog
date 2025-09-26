import { Injectable } from '@angular/core';
import { Observable } from 'rxjs';
import { User } from '../models/user';
import { HttpClient } from '@angular/common/http';

@Injectable({
  providedIn: 'root'
})

export class AuthService {
    private apiUrl = 'http://localhost:8080/auth';

  constructor(private http: HttpClient) {}

  public register(user: User): Observable<any> {
    return this.http.post(`${this.apiUrl}/register`, user);
  }
}
