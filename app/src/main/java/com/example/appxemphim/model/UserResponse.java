package com.example.appxemphim.model;

public class UserResponse {
    private int id;
    private String token;
    private String name;
    private String email;
    private String avatar;

    // Constructors
    public UserResponse() {
    }

    public UserResponse(int id, String token, String name, String email, String avatar) {
        this.id = id;
        this.token = token;
        this.name = name;
        this.email = email;
        this.avatar = avatar;
    }

    // Getter methods
    public int getId() {
        return id;
    }

    public String getToken() {
        return token;
    }

    public String getName() {
        return name;
    }

    public String getEmail() {
        return email;
    }

    public String getAvatar() {
        return avatar;
    }

    // Setter methods
    public void setId(int id) {
        this.id = id;
    }

    public void setToken(String token) {
        this.token = token;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public void setAvatar(String avatar) {
        this.avatar = avatar;
    }
}


