package com.umt.sprint2.model;

import java.io.Serializable;

public class User implements Serializable {
    private final String username;
    private String password;
    private final Role role;
    private final String studentId; // nullable when not a student

    public User(String username, String password, Role role, String studentId) {
        this.username = username;
        this.password = password;
        this.role = role;
        this.studentId = studentId;
    }

    public String getUsername() {
        return username;
    }

    public Role getRole() {
        return role;
    }

    public String getStudentId() {
        return studentId;
    }

    public boolean passwordMatches(String rawPassword) {
        return password != null && password.equals(rawPassword);
    }

    public void setPassword(String password) {
        this.password = password;
    }
}
