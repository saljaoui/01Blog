import { inject, Injectable } from '@angular/core';
import { environment } from '../../../environments/environment.development';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';
import { Post } from '../models/post';

@Injectable({
  providedIn: 'root'
})

export class PostService {
  private http = inject(HttpClient);
  private apiUrl = environment.api.posts;

  getAllPosts(): Observable<any> {
    return this.http.get<any>(this.apiUrl);
  }

  getPostById(id: string | null): Observable<any> {
    return this.http.get<any>(`${this.apiUrl}/${id}`);
  }
}
