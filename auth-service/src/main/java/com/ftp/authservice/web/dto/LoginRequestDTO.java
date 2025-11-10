package com.ftp.authservice.web.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;

public class LoginRequestDTO {
    @Email
    private String email;

    @NotBlank
    private String password;

    @NotBlank
    private String language;

    // Getters & Setters
    public String getLanguage(){
        return language;
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
}
