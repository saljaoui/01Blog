import { Routes } from '@angular/router';
import { Dashboard } from './features/dashboard/dashboard';
import { Login } from './features/auth/login/login';
import { Register } from './features/auth/register/register';
import { Admin } from './features/admin/admin';
import { MyProfile } from './features/profile/my-profile/my-profile';
import { UserProfile } from './features/profile/user-profile/user-profile';
import { CreatePost } from './features/posts/create-post/create-post';
import { Notifications } from './features/notifications/notifications';

export const routes: Routes = [
    { path: "", component: Dashboard },
    { path: "login", component: Login },
    { path: "register", component: Register },
    { path: "admin", component: Admin },
    { path: "my-profile", component: MyProfile },
    { path: "user-profile", component: UserProfile },
    { path: "create-post", component: CreatePost },
    { path: "notifications", component: Notifications },
];
