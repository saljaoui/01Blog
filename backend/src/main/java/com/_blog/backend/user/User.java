package com._blog.backend.user;

public class User {
    private String uuid;
    private String username;
    private String password;
    private String email;

    // Constructors
    public User() { }
    public User(String uuid, String username, String password) {
        this.uuid = uuid;
        this.username = username;
        this.password = password;
        // this.email = email;
    }

    // Getters
    public String getId() { return uuid; }
    public String getUsername() { return username; }
    public String getPassword() { return password; }
    public String getEmail() { return email; }

    // Setters
    public void setId(String uuid) { this.uuid = uuid; }
    public void setUsername(String username) { this.username = username; }
    public void setPassword(String password) { this.password = password; }
    public void setEmail(String email) { this.email = email; }
}
