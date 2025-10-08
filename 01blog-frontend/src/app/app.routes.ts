import { Routes } from '@angular/router';
import { Login } from './features/auth/login/login';
import { Register } from './features/auth/register/register';
import { Welcome } from './features/dashboard/welcome/welcome';
import { AuthLayout } from './layouts/auth-layout/auth-layout';
import { MainLayout } from './layouts/main-layout/main-layout';
import { Home } from './features/dashboard/home/home';
import { MyProfile } from './features/profile/my-profile/my-profile';
import { AdminDashboard } from './features/admin/admin-dashboard/admin-dashboard';
import { AdminLayout } from './layouts/admin-layout/admin-layout';
import { AdminUsers } from './features/admin/admin-users/admin-users';
import { AdminPosts } from './features/admin/admin-posts/admin-posts';
import { AdminReports } from './features/admin/admin-reports/admin-reports';
import { SavedPosts } from './features/saved-posts/saved-posts';
// import { AdminUsers } from './features/admin/admin-users/admin-users';
// import { AdminPosts } from './features/admin/admin-posts/admin-posts';
// import { AdminReports } from './features/admin/admin-reports/admin-reports';
// Import your guards when ready
// import { AuthGuard } from './guards/auth.guard';
// import { AdminGuard } from './guards/admin.guard';

export const routes: Routes = [
  {
    path: '',
    component: AuthLayout,
    children: [
      { path: '', component: Welcome },
      { path: 'register', component: Register },
      { path: 'login', component: Login },
    ]
  },
  {
    path: '',
    component: MainLayout,
    children: [
      { path: 'home', component: Home },
      { path: 'profile', component: MyProfile },
      { path: 'saved-posts', component: SavedPosts },
      {
        path: 'admin',
        component: AdminLayout,
        children: [
          { path: '', component: AdminDashboard },
          { path: 'users', component: AdminUsers },
          { path: 'posts', component: AdminPosts },
          { path: 'reports', component: AdminReports },
        ]
      }
    ]
  },
  
  // Wildcard route for 404
  { path: '**', redirectTo: '' }
];