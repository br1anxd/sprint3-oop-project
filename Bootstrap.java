package com.umt.sprint2.logic;

import com.umt.sprint2.data.DataStore;
import com.umt.sprint2.data.FilePersistence;
import com.umt.sprint2.data.repo.CourseRepository;
import com.umt.sprint2.data.repo.StudentRepository;
import com.umt.sprint2.data.repo.UserRepository;
import com.umt.sprint2.model.Course;
import com.umt.sprint2.model.Role;
import com.umt.sprint2.model.Student;
import com.umt.sprint2.model.User;

import java.io.IOException;

public class Bootstrap {
    public static void seedIfEmpty(DataStore store, FilePersistence persistence) {
        if (!store.getUsersByUsername().isEmpty() && !store.getCoursesByCode().isEmpty()) {
            return;
        }

        StudentRepository studentRepo = new StudentRepository(store);
        CourseRepository courseRepo = new CourseRepository(store);
        UserRepository userRepo = new UserRepository(store);

        // Initial demo data
        Student s1 = new Student("S001", "Arbana Berisha", "arbana@umt.com");
        Student s2 = new Student("S002", "Brian Doci", "brian@umt.com");
        studentRepo.upsert(s1);
        studentRepo.upsert(s2);

        courseRepo.upsert(new Course("CS101", "Introduction to Programming"));
        courseRepo.upsert(new Course("OOP101", "Object Oriented Programming"));
        courseRepo.upsert(new Course("MA101", "Discrete Mathematics"));

        // Users (plain-text password for demo purposes)
        userRepo.upsert(new User("s001", "1234", Role.STUDENT, "S001"));
        userRepo.upsert(new User("s002", "1234", Role.STUDENT, "S002"));
        userRepo.upsert(new User("admin", "admin", Role.ADMIN, null));

        try {
            persistence.save(store);
        } catch (IOException ignored) {
            // if seed can't save, app can still run, but won't persist
        }
    }
}
