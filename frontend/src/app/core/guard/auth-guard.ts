import { CanActivateFn, Router } from '@angular/router';
import { AuthService } from '../services/auth.service';
import { inject } from '@angular/core';

export const authGuard: CanActivateFn = (route, state) => {
  const authService = inject(AuthService);
  const router = inject(Router);

  console.log('🛡️ AuthGuard triggered for route:', state.url);

  if (authService.isAuthenticated()) {
    console.log('✅ User is authenticated');
    return true;
  } else {
    console.log('❌ User not authenticated — redirecting to login');
    router.navigate(['/login'], { 
      queryParams: { returnUrl: state.url } 
    });
    return false;
  }
};
