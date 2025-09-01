import { Routes } from '@angular/router';
import { Dashboard } from './features/dashboard/dashboard';
import { Login } from './features/auth/login/login';
import { Register } from './features/auth/register/register';
import { Admin } from './features/admin/admin';

export const routes: Routes = [
    { path: "", component: Dashboard },
    { path: "login", component: Login },
    { path: "register", component: Register },
    { path: "admin", component: Admin },
];
