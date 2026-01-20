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

public class AppContext {
    private final FilePersistence persistence;
    private final DataStore store;

    public final UserRepository userRepo;
    public final StudentRepository studentRepo;
    public final CourseRepository courseRepo;
    public final EnrollmentRepository enrollmentRepo;

    public final AuthService authService;
    public final EnrollmentService enrollmentService;

    private User currentUser;

    public AppContext(FilePersistence persistence) {
        this.persistence = persistence;
        this.store = persistence.loadOrCreate();

        this.userRepo = new UserRepository(store);
        this.studentRepo = new StudentRepository(store);
        this.courseRepo = new CourseRepository(store);
        this.enrollmentRepo = new EnrollmentRepository(store);

        seedIfEmpty();

        this.authService = new AuthService(userRepo);
        this.enrollmentService = new EnrollmentService(store, studentRepo, courseRepo, enrollmentRepo, persistence);

        // Ensure seed data is persisted the first time
        try {
            persistence.save(store);
        } catch (IOException ignored) {
        }
    }

    private void seedIfEmpty() {
        if (!store.getStudentsById().isEmpty() || !store.getCoursesByCode().isEmpty() || !store.getUsersByUsername().isEmpty()) {
            return;
        }

        // Students
        Student s1 = new Student("S001", "Arbana Berisha", "arbana@umt.com");
        Student s2 = new Student("S002", "Brian Doci", "brian@umt.com");
        studentRepo.upsert(s1);
        studentRepo.upsert(s2);

        // Courses
        courseRepo.upsert(new Course("CS101", "Introduction to Programming"));
        courseRepo.upsert(new Course("OOP101", "Object Oriented Programming"));
        courseRepo.upsert(new Course("MA101", "Discrete Mathematics"));

        // Users (login)
        userRepo.upsert(new User("s001", "1234", Role.STUDENT, "S001"));
        userRepo.upsert(new User("s002", "1234", Role.STUDENT, "S002"));
        userRepo.upsert(new User("admin", "admin", Role.ADMIN, null));
    }

    public User getCurrentUser() {
        return currentUser;
    }

    public void setCurrentUser(User currentUser) {
        this.currentUser = currentUser;
    }

    public void save() throws IOException {
        persistence.save(store);
    }
}
