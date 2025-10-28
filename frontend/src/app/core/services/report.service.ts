import { inject, Injectable } from '@angular/core';
import { environment } from '../../../environments/environment.development';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';

@Injectable({
    providedIn: 'root'
})

export class ReportService {
    private http = inject(HttpClient);
    private apiUrl = environment.api.posts;
    reportUser(reportedUserId: string, reason: string): Observable<void> {
        return this.http.post<void>(`${environment.api.reports}`, { reportedUserId, reason, type: "USER" });
    }
}