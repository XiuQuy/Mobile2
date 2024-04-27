package com.example.appxemphim.model;

public class ForgotDTO {
    private int code;
    private String email;
    private String password;
    private String passwordConfirm;

    public ForgotDTO(int code, String email, String password, String passwordConfirm) {
        this.code = code;
        this.email = email;
        this.password = password;
        this.passwordConfirm = passwordConfirm;
    }

    public ForgotDTO(String email) {
        this.email = email;
    }

    public ForgotDTO(int code, String email) {
        this.code = code;
        this.email = email;
    }

    // Getters and setters
    public int getCode() {
        return code;
    }

    public void setCode(int code) {
        this.code = code;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getPasswordConfirm() {
        return passwordConfirm;
    }

    public void setPasswordConfirm(String passwordConfirm) {
        this.passwordConfirm = passwordConfirm;
    }
}
