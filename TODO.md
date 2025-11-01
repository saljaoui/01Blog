# TODO: Fix Avatar Display Issues

## Backend Fixes
- [x] Update PostService.java to include authorAvatar in all PostResponse builders (getAllPosts, getPostsByUser, getPostById, toPostResponse)
- [x] Standardize default avatar URL in UserService.java to use consistent path (e.g., /api/uploads/avatars/default-avatar.png)

## Frontend Fixes
- [x] Update sidebar-right.html to use user.avatarUrl instead of hardcoded image
- [x] Check and fix notifications.html if it displays avatars (read notifications.ts and notifications.html)

## Testing
- [ ] Verify avatars display correctly in sidebar left, sidebar right, home posts, and notifications
