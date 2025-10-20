import { HttpClient } from '@angular/common/http';
import { inject, Injectable } from '@angular/core';
import { Observable } from 'rxjs';
import { User } from '../models/user';

@Injectable({
  providedIn: 'root'
})
export class UserService {
    private readonly apiUrl = 'http://localhost:8080/api/users';
    private http = inject(HttpClient);

   getCurrentUser(): Observable<User> {
    return this.http.get<User>(`${this.apiUrl}/me`);
   } 
}
