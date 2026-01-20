package com.umt.sprint2.data;

import com.umt.sprint2.model.Course;
import com.umt.sprint2.model.Student;
import com.umt.sprint2.model.User;

import java.io.Serializable;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Data root object persisted to disk.
 *
 * Demonstrates meaningful collections usage:
 * - HashMap for fast lookup (students, courses, users, enrollments)
 * - List for ordered enrollments
 */
public class DataStore implements Serializable {
    private final Map<String, Student> studentsById = new HashMap<>();
    private final Map<String, Course> coursesByCode = new HashMap<>();
    private final Map<String, User> usersByUsername = new HashMap<>();

    // studentId -> ordered list of course codes
    private final Map<String, List<String>> enrollmentsByStudentId = new HashMap<>();

    public Map<String, Student> getStudentsById() {
        return studentsById;
    }

    public Map<String, Course> getCoursesByCode() {
        return coursesByCode;
    }

    public Map<String, User> getUsersByUsername() {
        return usersByUsername;
    }

    public Map<String, List<String>> getEnrollmentsByStudentId() {
        return enrollmentsByStudentId;
    }
}
