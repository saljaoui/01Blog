import { HttpErrorResponse, HttpInterceptorFn } from '@angular/common/http';
import { inject } from '@angular/core';
import { AuthService } from '../services/auth.service';
import { catchError, switchMap, throwError } from 'rxjs';

export const authInterceptor: HttpInterceptorFn = (req, next) => {
  const authService = inject(AuthService);
  
  // console.log('🔵 [INTERCEPTOR] Request URL:', req.url);

  // Skip auth endpoints
  if (req.url.includes('/auth/login') ||
    req.url.includes('/auth/register') ||
    req.url.includes('/auth/refresh')) {
    return next(req);
  }

  // Add token to request
  const accessToken = authService.getAccessToken();
  // console.log('🔵 [INTERCEPTOR] accessToken:', accessToken);
  if (accessToken) {
    req = req.clone({
      setHeaders: {
        Authorization: `Bearer ${accessToken}`
      }
    });
  }

  return next(req).pipe(
    catchError((error: HttpErrorResponse) => {
      // If 401 error, try to refresh token
      if (error.status === 401) {
        return authService.refreshToken().pipe(
          switchMap((response: any) => {
            // Retry the original request with new token
            const newToken = authService.getAccessToken();
            const clonedRequest = req.clone({
              setHeaders: {
                Authorization: `Bearer ${newToken}`
              }
            });
            return next(clonedRequest);
          }),
          catchError((refreshError) => {
            // Refresh failed, logout user
            authService.logout();
            return throwError(() => refreshError);
          })
        );
      }

      return throwError(() => error);
    })
  );
};