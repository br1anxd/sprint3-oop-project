package com.umt.sprint2.model;

import java.io.Serializable;

public class Course implements Serializable {
    private final String code;
    // name/title of the course
    private String title;

    public Course(String code, String title) {
        this.code = code;
        this.title = title;
    }

    public String getCode() {
        return code;
    }

    public String getTitle() { return title; }

    // Friendly alias used by the GUI ("Name")
    public String getName() { return title; }

    public void setTitle(String title) { this.title = title; }

    public void setName(String name) { this.title = name; }

    @Override
    public String toString() {
        return code + " - " + title;
    }
}
