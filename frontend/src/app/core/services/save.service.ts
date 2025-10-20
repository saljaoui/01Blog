import { HttpClient } from '@angular/common/http';
import { inject, Injectable } from '@angular/core';
import { Observable } from 'rxjs';
import { environment } from '../../../environments/environment.development';

@Injectable({
  providedIn: 'root'
})
export class saveService {
  private apiUrl = environment.api.saved;
  private http = inject(HttpClient);
  
    toggleSave(postId: String): Observable<any> {
      return this.http.post(`${this.apiUrl}`, {postId});
    }
}
