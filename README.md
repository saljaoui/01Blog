# 01Blog – Social Blogging Platform

01Blog is a fullstack blogging platform where students can share their learning journey, publish posts, interact with others, and build a learning community. Built with **Spring Boot** (Java) backend and **Angular** frontend with secure JWT authentication and role-based access control.

---

## 📸 Project Preview

### Welcome Page
![Welcome Page](README_ASSETS/wlc.png)

### Login Page
![Login Page](README_ASSETS/login.png)

### Home Page
![Home Page](README_ASSETS/home.png)

### Admin Dashboard
![Admin Dashboard](README_ASSETS/admin.png)

---

## ✨ Features

### 👤 User Features

- **Authentication**: Register and login using JWT authentication
- **Post Management**: Create, edit, and delete posts with full control
- **Media Uploads**: Upload and attach images or videos to posts
- **Social Interactions**: Like and comment on posts from other users
- **User Discovery**: View and follow other users' profiles
- **Notifications**: Receive real-time notifications from subscribed users
- **Personal Profile**: Dedicated user profile page with profile management
- **Reporting System**: Report users for inappropriate behavior with reasons and timestamps

### 🛡️ Admin Features

- **Admin Dashboard**: Dedicated admin interface with clear navigation
- **User Management**: View, manage, delete, or ban all users
- **Content Moderation**: Remove or hide inappropriate posts
- **Report Management**: Review all submitted reports with timestamps and reasons
- **Access Control**: All admin actions protected with role-based authorization
- **Admin Analytics**: Dashboard displaying key metrics (posts count, reported users, etc.)

---

## 🛠️ Tech Stack

### Backend
- **Java Spring Boot** – REST API framework
- **Spring Security + JWT** – Authentication and authorization
- **JPA / Hibernate** – Object-relational mapping
- **PostgreSQL / MySQL** – Database management
- **File Storage** – Local or AWS S3 for media handling

### Frontend
- **Angular** – Modern web framework
- **Angular Material & Bootstrap** – UI components and styling
- **RxJS** – Reactive programming for asynchronous operations
- **Responsive Design** – Mobile-friendly interface

### DevOps
- **Docker & Docker Compose** – Containerization
- **Git** – Version control

---

## ✅ Implementation Checklist

### General Architecture
- ✅ Clear and logical folder structure for organized codebase
- ✅ README file included with setup instructions and technologies
- ✅ Fullstack application with separate Spring Boot backend and Angular frontend
- ✅ Source code organized with proper separation of concerns

### API & Communication
- ✅ Communication between frontend and backend through REST APIs
- ✅ RESTful endpoint design with proper HTTP methods
- ✅ JSON request/response format

### Authentication & Security
- ✅ User authentication implemented using JWT tokens
- ✅ Passwords stored with BCrypt hashing algorithm
- ✅ Role-based access control (RBAC) for admin and user roles
- ✅ User sessions and authentication tokens securely managed
- ✅ Protected admin-only routes with access control enforcement
- ✅ Input sanitization to prevent SQL injection and XSS attacks

### User Functionality
- ✅ Post CRUD operations with appropriate access control
- ✅ Media upload and storage with security measures
- ✅ Like and comment functionality on posts
- ✅ User follow/subscribe system with notifications
- ✅ Automatic notification generation when followed users publish posts
- ✅ User profile management and visibility

### Data Validation & Error Handling
- ✅ All actions validated on backend (posting, liking, commenting, subscribing, reporting)
- ✅ Error handling on both backend and frontend
- ✅ Invalid routes and actions handled with proper error messages
- ✅ Edge cases handled (empty posts, invalid files, duplicate usernames)

### Database Design
- ✅ Database relationships correctly set up (users, posts, comments, likes, reports)
- ✅ Reports saved with reasons and timestamps
- ✅ Proper foreign key relationships and data integrity

### Admin Functionality
- ✅ Admin can view all users, posts, and submitted reports
- ✅ Admin can delete or ban users with confirmation
- ✅ Admin can remove or hide inappropriate posts
- ✅ Admin can manage reports and take action
- ✅ Dedicated admin dashboard with clear navigation
- ✅ Admin actions require confirmation before execution

### Frontend (Angular)
- ✅ UI divided into Angular components with proper routing
- ✅ Angular Material and Bootstrap used for styling
- ✅ Responsive and mobile-friendly interface
- ✅ Media uploads previewed before submission
- ✅ User roles reflected in the interface (admin tools hidden from regular users)
- ✅ Smooth user interactions (like, comment, view posts)
- ✅ Visual feedback for all actions (post, report, subscribe)
- ✅ User-friendly UI for reporting with reason input

### Post Management
- ✅ Users can create, edit, and delete posts
- ✅ Media, timestamps, likes, and comments displayed on posts
- ✅ Like and comment functionality on all posts
- ✅ Deleted posts and comments removed correctly
- ✅ Uploaded files retrievable without corruption

### Quality Assurance
- ✅ Application functional under multiple concurrent users
- ✅ Browser console free of errors
- ✅ Proper error handling and user feedback

### 🎁 Bonus Features
- ✅ Real-time notifications when subscribed users publish posts
- ✅ Infinite scrolling on post feed
- ✅ Dark mode toggle available
- ✅ Admin analytics dashboard (posts count, reported users)
- ✅ Markdown support in post editor

---

## 🚀 Getting Started

### Prerequisites
- Java 11+ and Maven
- Node.js and npm
- PostgreSQL or MySQL database
- Docker (optional)

### Backend Setup

1. Clone the repository
```bash
git clone https://github.com/saljaoui/01blog.git
cd 01blog/backend
```

2. Configure database in `application.properties`
```properties
spring.datasource.url=jdbc:postgresql://localhost:5432/01blog
spring.datasource.username=your_username
spring.datasource.password=your_password
```

3. Build and run
```bash
mvn clean install
mvn spring-boot:run
```

### Frontend Setup

1. Navigate to frontend directory
```bash
cd ../frontend
```

2. Install dependencies
```bash
npm install
```

3. Run development server
```bash
ng serve
```

4. Open `http://localhost:4200` in your browser

### Docker Deployment

```bash
docker-compose up -d
```

---

## 📁 Project Structure

```
01blog/
├── backend/                    # Spring Boot REST API
│   ├── src/
│   │   ├── main/java/
│   │   │   ├── controller/     # API endpoints
│   │   │   ├── service/        # Business logic
│   │   │   ├── repository/     # Database access
│   │   │   ├── entity/         # JPA entities
│   │   │   ├── security/       # JWT & Spring Security
│   │   │   └── config/         # Configuration
│   │   └── resources/
│   │       └── application.properties
│   └── pom.xml
│
├── frontend/                   # Angular application
│   ├── src/
│   │   ├── app/
│   │   │   ├── components/     # Angular components
│   │   │   ├── services/       # HTTP services
│   │   │   ├── guards/         # Route guards
│   │   │   ├── models/         # TypeScript models
│   │   │   └── interceptors/   # HTTP interceptors
│   │   └── assets/
│   └── angular.json
│
└── docker-compose.yml
```

---

## 🔒 Security Features

- **JWT Authentication**: Stateless, secure token-based authentication
- **Password Security**: BCrypt hashing for password storage
- **RBAC**: Role-based access control with @PreAuthorize annotations
- **Input Validation**: Server-side validation of all user inputs
- **SQL Injection Prevention**: Parameterized queries via JPA
- **XSS Protection**: Angular's built-in XSS prevention mechanisms
- **CORS Configuration**: Properly configured Cross-Origin Resource Sharing

---

## 🧪 Testing

The application has been tested for:
- Multiple concurrent users
- Edge cases (empty posts, invalid files, duplicate usernames)
- Browser compatibility and console errors
- Invalid routes and error handling
- Admin functionality and access control
- Media upload and retrieval integrity

---

## 📊 Key Endpoints

### Authentication
- `POST /api/auth/register` – User registration
- `POST /api/auth/login` – User login

### Posts
- `GET /api/posts` – Get all posts (with pagination)
- `POST /api/posts` – Create new post
- `PUT /api/posts/{id}` – Edit post
- `DELETE /api/posts/{id}` – Delete post

### Interactions
- `POST /api/posts/{id}/like` – Like a post
- `POST /api/posts/{id}/comments` – Comment on post
- `GET /api/posts/{id}/comments` – Get post comments

### Users
- `GET /api/users/{id}` – Get user profile
- `POST /api/users/{id}/follow` – Follow user
- `GET /api/users/{id}/followers` – Get followers

### Reports
- `POST /api/reports` – Submit a report
- `GET /api/reports` – Get reports (admin only)
- `PUT /api/reports/{id}` – Update report status (admin only)

### Admin
- `GET /api/admin/users` – Get all users (admin only)
- `DELETE /api/admin/users/{id}` – Delete user (admin only)
- `POST /api/admin/users/{id}/ban` – Ban user (admin only)
- `DELETE /api/admin/posts/{id}` – Remove post (admin only)
- `GET /api/admin/analytics` – Get admin analytics (admin only)

---

## 👨‍💻 Author

**Soufian Aljaoui**

- GitHub: [https://github.com/saljaoui](https://github.com/saljaoui)
- LinkedIn: [LinkedIn Profile](https://linkedin.com/in/saljaoui)

---

## 📄 License

This project is for educational purposes for **Zone01 Oujda**.

---

## 🤝 Contributing

Contributions are welcome! Please feel free to submit issues and pull requests.

---

## 📝 Changelog

### Version 1.0
- ✅ Initial release with all core features
- ✅ JWT authentication and role-based access control
- ✅ Full post CRUD operations
- ✅ Social interactions (likes, comments)
- ✅ Follow/subscribe and notifications system
- ✅ Reporting system with admin review
- ✅ Admin dashboard with user and content management
- ✅ Real-time notifications
- ✅ Dark mode support
- ✅ Infinite scrolling on feed
- ✅ Markdown editor support

---

**Last Updated**: November 2025