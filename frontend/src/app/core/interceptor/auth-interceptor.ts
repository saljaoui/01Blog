import { HttpErrorResponse, HttpInterceptorFn } from '@angular/common/http';
import { inject } from '@angular/core';
import { catchError, switchMap, throwError } from 'rxjs';
import { AuthService } from '../services/auth.service';

export const authInterceptor: HttpInterceptorFn = (req, next) => {
 const authService = inject(AuthService);
  const accessToken = localStorage.getItem('accessToken');

  // add token to request
  let authReq = accessToken ? req.clone({
    setHeaders: { Authorization: `Bearer ${accessToken}` }
  }) : req;

  return next(authReq).pipe(
    catchError((error: HttpErrorResponse) => {

      if (error.status === 401) {
        return authService.refreshToken().pipe(
          switchMap((res: any) => {

            localStorage.setItem('accessToken', res.accessToken);
            localStorage.setItem('refreshToken', res.refreshToken);

            const newReq = req.clone({
              setHeaders: { Authorization: `Bearer ${res.accessToken}` }
            });
            return next(newReq);
          })
        );
      }
      return throwError(() => error);
    })
  );
};
