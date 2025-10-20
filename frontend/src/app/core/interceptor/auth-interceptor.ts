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
  console.log('🔵 [INTERCEPTOR] accessToken:', accessToken);
  if (accessToken) {
    req = req.clone({
      setHeaders: {
        Authorization: `Bearer ${accessToken}`
      }
    });
  }

return next(req).pipe(
    catchError((error: HttpErrorResponse) => {
      console.log("🔴 [INTERCEPTOR] Error:", error);
      
      if (error.status === 401) {
        const errorType = error.error?.error || '';
        
        // Check if it's JWT expired error
        if (errorType === 'JWT_EXPIRED') {
          console.log("🟡 [INTERCEPTOR] JWT expired, attempting refresh...");
          
          return authService.refreshToken().pipe(
            switchMap((response: any) => {
              console.log("✅ [INTERCEPTOR] Token refreshed successfully");
              
              // Retry the original request with new token
              const newToken = authService.getAccessToken();
              const clonedRequest = req.clone({
                setHeaders: {
                  Authorization: `Bearer ${newToken}`
                }
              });
              return next(clonedRequest);
            }),
            catchError((refreshError: HttpErrorResponse) => {
              // Check if refresh token expired
              const refreshErrorType = refreshError.error?.error || '';
              
              if (refreshErrorType === 'REFRESH_TOKEN_EXPIRED') {
                console.log("🔴 [INTERCEPTOR] Refresh token expired - logging out");
              } else {
                console.log("🔴 [INTERCEPTOR] Refresh failed - logging out");
              }
              
              // Logout user
              authService.logout();
              return throwError(() => refreshError);
            })
          );
        } 
        // If JWT is invalid or any other 401 error
        else {
          console.log("🔴 [INTERCEPTOR] Invalid token - logging out");
          authService.logout();
          return throwError(() => error);
        }
      }

      return throwError(() => error);
    })
  );
};
