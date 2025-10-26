import { inject, Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Router } from '@angular/router';
import { Observable, throwError } from 'rxjs';
import { tap, catchError } from 'rxjs/operators';
import { UserLogin, UserRegister } from '../models/user';
import { environment } from '../../../environments/environment.development';

@Injectable({
    providedIn: 'root'
})
export class AdminService {
    private http = inject(HttpClient);
    private apiUrl = environment.api.reports;

    getStatus(): Observable<any> {
         return this.http.get<any>(`${this.apiUrl}/report-status`);
    }
}