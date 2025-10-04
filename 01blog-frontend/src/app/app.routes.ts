import { Routes } from '@angular/router';
import { Login } from './features/auth/login/login';
import { Register } from './features/auth/register/register';
import { Welcome } from './features/dashboard/welcome/welcome';
import { AuthLayout } from './layouts/auth-layout/auth-layout';
import { MainLayout } from './layouts/main-layout/main-layout';
import { Home } from './features/dashboard/home/home';

export const routes: Routes = [
    {
    path: '',
    component: MainLayout,
    children: [
      { path: '', component: Welcome },
      { path: 'home', component: Home }
    ]
  },
  {
    path: '',
    component: AuthLayout,
    children: [
    { path: 'register', component: Register },
    { path: 'login', component: Login }
    ]
  },
];
