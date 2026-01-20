package com.umt.sprint2.logic;

import com.umt.sprint2.data.DataStore;
import com.umt.sprint2.data.FilePersistence;
import com.umt.sprint2.data.repo.CourseRepository;
import com.umt.sprint2.data.repo.EnrollmentRepository;
import com.umt.sprint2.data.repo.StudentRepository;
import com.umt.sprint2.data.repo.UserRepository;
import com.umt.sprint2.model.Course;
import com.umt.sprint2.model.Role;
import com.umt.sprint2.model.Student;
import com.umt.sprint2.model.User;

import java.io.IOException;
import java.util.List;
import java.util.Map;
import java.util.Objects;

/**
 * Admin use-cases: manage courses/students/users and view/modify enrollments.
 */
public class AdminService {
    private final DataStore store;
    private final UserRepository userRepo;
    private final StudentRepository studentRepo;
    private final CourseRepository courseRepo;
    private final EnrollmentRepository enrollmentRepo;
    private final FilePersistence persistence;

    public AdminService(DataStore store,
                        UserRepository userRepo,
                        StudentRepository studentRepo,
                        CourseRepository courseRepo,
                        EnrollmentRepository enrollmentRepo,
                        FilePersistence persistence) {
        this.store = store;
        this.userRepo = userRepo;
        this.studentRepo = studentRepo;
        this.courseRepo = courseRepo;
        this.enrollmentRepo = enrollmentRepo;
        this.persistence = persistence;
    }

    public List<Course> listCourses() {
        return courseRepo.findAllSortedByCode();
    }

    public List<Student> listStudents() {
        return studentRepo.findAllSortedById();
    }

    public List<User> listUsers() {
        return userRepo.findAllSortedByUsername();
    }

    public Map<String, List<String>> allEnrollmentsView() {
        return enrollmentRepo.allEnrollmentsView();
    }

    public void createOrUpdateCourse(String code, String name) throws AdminException {
        code = safeTrim(code).toUpperCase();
        name = safeTrim(name);

        if (code.isEmpty()) throw new AdminException("Course code is required.");
        if (name.isEmpty()) throw new AdminException("Course name is required.");
        if (!code.matches("[A-Z0-9]{2,10}")) {
            throw new AdminException("Course code must be 2-10 characters (A-Z, 0-9). Example: OOP101");
        }

        courseRepo.upsert(new Course(code, name));
        saveOrThrow();
    }

    public void deleteCourse(String code) throws AdminException {
        code = safeTrim(code).toUpperCase();
        if (code.isEmpty()) throw new AdminException("Select a course to delete.");

        boolean removed = courseRepo.deleteByCode(code);
        if (!removed) throw new AdminException("Course not found: " + code);

        // Keep enrollments consistent
        enrollmentRepo.removeCourseEverywhere(code);
        saveOrThrow();
    }

    /**
     * Creates a student + a linked student user account (Role.STUDENT).
     */
    public void createStudentAccount(String studentId,
                                     String fullName,
                                     String email,
                                     String username,
                                     String password) throws AdminException {
        studentId = safeTrim(studentId).toUpperCase();
        fullName = safeTrim(fullName);
        email = safeTrim(email);
        username = safeTrim(username).toLowerCase();
        password = password == null ? "" : password;

        if (studentId.isEmpty()) throw new AdminException("Student ID is required.");
        if (fullName.isEmpty()) throw new AdminException("Full name is required.");
        if (username.isEmpty()) throw new AdminException("Username is required.");
        if (password.isEmpty()) throw new AdminException("Password is required.");

        if (studentRepo.findById(studentId).isPresent()) {
            throw new AdminException("Student ID already exists: " + studentId);
        }
        if (userRepo.findByUsername(username).isPresent()) {
            throw new AdminException("Username already exists: " + username);
        }

        Student s = new Student(studentId, fullName, email);
        studentRepo.upsert(s);
        userRepo.upsert(new User(username, password, Role.STUDENT, studentId));
        saveOrThrow();
    }

    public void resetUserPassword(String username, String newPassword) throws AdminException {
        // Use new local variables so they are effectively-final for lambdas.
        final String uname = safeTrim(username).toLowerCase();
        final String pwd = newPassword == null ? "" : newPassword;
        if (uname.isEmpty()) throw new AdminException("Select a user.");
        if (pwd.isEmpty()) throw new AdminException("New password is required.");

        User u = userRepo.findByUsername(uname).orElseThrow(() -> new AdminException("User not found: " + uname));
        if (u.getRole() == Role.ADMIN && Objects.equals(u.getUsername(), "admin")) {
            // allow, but keep simple
        }
        u.setPassword(pwd);
        userRepo.upsert(u);
        saveOrThrow();
    }

    public void deleteStudentAndUser(String studentId) throws AdminException {
        // Use a new local variable so it is effectively-final for lambdas.
        final String sid = safeTrim(studentId).toUpperCase();
        if (sid.isEmpty()) throw new AdminException("Select a student.");

        boolean studentRemoved = studentRepo.deleteById(sid);
        if (!studentRemoved) throw new AdminException("Student not found: " + sid);

        // remove linked user(s)
        store.getUsersByUsername().values().removeIf(u -> u.getStudentId() != null && u.getStudentId().equals(sid));
        enrollmentRepo.removeStudent(sid);
        saveOrThrow();
    }

    public void forceUnenroll(String studentId, String courseCode) throws AdminException {
        studentId = safeTrim(studentId).toUpperCase();
        courseCode = safeTrim(courseCode).toUpperCase();
        if (studentId.isEmpty() || courseCode.isEmpty()) throw new AdminException("Student and course are required.");

        boolean removed = enrollmentRepo.removeEnrollment(studentId, courseCode);
        if (!removed) throw new AdminException("Enrollment not found for " + studentId + " in " + courseCode);
        saveOrThrow();
    }

    private void saveOrThrow() throws AdminException {
        try {
            persistence.save(store);
        } catch (IOException e) {
            throw new AdminException("Failed to save data: " + e.getMessage());
        }
    }

    private String safeTrim(String s) {
        return s == null ? "" : s.trim();
    }
}
