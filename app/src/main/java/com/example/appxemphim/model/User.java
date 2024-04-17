package com.example.appxemphim.model;

public class User {
    private int id;
    private String name;
    private String token;
    private String avatar;
    private String email;
    private String tagSocialNetwork;

    public String getTagSocialNetwork() {
        return tagSocialNetwork;
    }

    public void setTagSocialNetwork(String tagSocialNetwork) {
        this.tagSocialNetwork = tagSocialNetwork;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }

    public String getAvatar() {
        return avatar;
    }

    public void setAvatar(String avatar) {
        this.avatar = avatar;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }
}
