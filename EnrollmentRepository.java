package com.umt.sprint2.data.repo;

import com.umt.sprint2.data.DataStore;

import java.util.*;

/**
 * Data-layer repository for enrollments.
 *
 * Under the hood we store:
 *   studentId -> ordered List of course codes
 *
 * This demonstrates meaningful collections:
 * - HashMap for fast lookup by studentId
 * - List for preserving enrollment order
 */
public class EnrollmentRepository {
    private final DataStore store;

    public EnrollmentRepository(DataStore store) {
        this.store = store;
    }

    public List<String> getCourseCodesForStudent(String studentId) {
        return store.getEnrollmentsByStudentId().getOrDefault(studentId, new ArrayList<>());
    }

    /**
     * @return true if newly added, false if already enrolled
     */
    public boolean addEnrollment(String studentId, String courseCode) {
        store.getEnrollmentsByStudentId().putIfAbsent(studentId, new ArrayList<>());
        List<String> list = store.getEnrollmentsByStudentId().get(studentId);
        if (list.contains(courseCode)) {
            return false;
        }
        list.add(courseCode);
        return true;
    }

    /**
     * @return true if removed, false if it wasn't enrolled
     */
    public boolean removeEnrollment(String studentId, String courseCode) {
        List<String> list = store.getEnrollmentsByStudentId().get(studentId);
        if (list == null) {
            return false;
        }
        boolean removed = list.remove(courseCode);
        if (list.isEmpty()) {
            store.getEnrollmentsByStudentId().remove(studentId);
        }
        return removed;
    }

    /**
     * Returns a read-only view of all enrollments for admin/reporting.
     */
    public Map<String, List<String>> allEnrollmentsView() {
        return Collections.unmodifiableMap(store.getEnrollmentsByStudentId());
    }

    /**
     * Remove a course from every student's enrollment list.
     */
    public void removeCourseEverywhere(String courseCode) {
        for (Map.Entry<String, List<String>> e : store.getEnrollmentsByStudentId().entrySet()) {
            e.getValue().removeIf(code -> Objects.equals(code, courseCode));
        }
        // Clean up empty lists
        store.getEnrollmentsByStudentId().entrySet().removeIf(e -> e.getValue() == null || e.getValue().isEmpty());
    }

    /**
     * Remove all enrollments for a student.
     */
    public void removeStudent(String studentId) {
        store.getEnrollmentsByStudentId().remove(studentId);
    }
}
