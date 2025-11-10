package com.ftp.authservice.application.command;

public class RegisterUserCommand {
    private final String firstName;
    private final String fatherName;
    private final String surname;
    private final String fullName;
    private final String email;
    private final String password;
    private final String confirmPassword;
    private final String type;
    private final String language;

    public RegisterUserCommand(String firstName, String fatherName, String surname, String fullName, 
                                String email, String password, String confirmPassword, String type, String language) {
        this.firstName = firstName;
        this.fatherName = fatherName;
        this.surname = surname;
        this.fullName = fullName;
        this.email = email;
        this.password = password;
        this.confirmPassword = confirmPassword;
        this.type = type;
        this.language = language;
    }

    public String getFirstName() {
        return firstName;
    }

    public String getFatherName() {
        return fatherName;
    }

    public String getSurname() {
        return surname;
    }

    public String getFullName() {
        return fullName;
    }

    public String getEmail() {
        return email;
    }

    public String getPassword() {
        return password;
    }

    public String getConfirmPassword() {
        return confirmPassword;
    }

    public String getType() {
        return type;
    }

    public String getLanguage() {
        return language;
    }
}
