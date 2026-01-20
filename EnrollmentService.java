package com.umt.sprint2.logic;

import com.umt.sprint2.data.FilePersistence;
import com.umt.sprint2.data.DataStore;
import com.umt.sprint2.data.repo.CourseRepository;
import com.umt.sprint2.data.repo.EnrollmentRepository;
import com.umt.sprint2.data.repo.StudentRepository;
import com.umt.sprint2.model.Course;

import java.io.IOException;
import java.util.List;
import java.util.stream.Collectors;

public class EnrollmentService {
    private final DataStore store;
    private final StudentRepository studentRepo;
    private final CourseRepository courseRepo;
    private final EnrollmentRepository enrollmentRepo;
    private final FilePersistence persistence;

    public EnrollmentService(DataStore store,
                             StudentRepository studentRepo,
                             CourseRepository courseRepo,
                             EnrollmentRepository enrollmentRepo,
                             FilePersistence persistence) {
        this.store = store;
        this.studentRepo = studentRepo;
        this.courseRepo = courseRepo;
        this.enrollmentRepo = enrollmentRepo;
        this.persistence = persistence;
    }

    public void enroll(String studentId, String courseCode) throws EnrollmentException {
        if (studentRepo.findById(studentId).isEmpty()) {
            throw new EnrollmentException("Student not found: " + studentId);
        }
        if (courseRepo.findByCode(courseCode).isEmpty()) {
            throw new EnrollmentException("Course not found: " + courseCode);
        }
        boolean added = enrollmentRepo.addEnrollment(studentId, courseCode);
        if (!added) {
            throw new EnrollmentException("You are already enrolled in " + courseCode);
        }

        try {
            persistence.save(store);
        } catch (IOException e) {
            throw new EnrollmentException("Failed to save data: " + e.getMessage());
        }
    }

    public void unenroll(String studentId, String courseCode) throws EnrollmentException {
        if (studentRepo.findById(studentId).isEmpty()) {
            throw new EnrollmentException("Student not found: " + studentId);
        }
        if (courseRepo.findByCode(courseCode).isEmpty()) {
            throw new EnrollmentException("Course not found: " + courseCode);
        }
        boolean removed = enrollmentRepo.removeEnrollment(studentId, courseCode);
        if (!removed) {
            throw new EnrollmentException("You are not enrolled in " + courseCode);
        }

        try {
            persistence.save(store);
        } catch (IOException e) {
            throw new EnrollmentException("Failed to save data: " + e.getMessage());
        }
    }

    public List<Course> listAllCourses() {
        return courseRepo.findAllSortedByCode();
    }

    public List<Course> listCoursesForStudent(String studentId) {
        List<String> codes = enrollmentRepo.getCourseCodesForStudent(studentId);
        return codes.stream()
                .map(courseRepo::findByCode)
                .flatMap(java.util.Optional::stream)
                .collect(Collectors.toList());
    }
}
