package com.umt.sprint2.data.repo;

import com.umt.sprint2.data.DataStore;
import com.umt.sprint2.model.Course;

import java.util.*;

public class CourseRepository {
    private final DataStore store;

    public CourseRepository(DataStore store) {
        this.store = store;
    }

    public Optional<Course> findByCode(String code) {
        return Optional.ofNullable(store.getCoursesByCode().get(code));
    }

    public void upsert(Course course) {
        store.getCoursesByCode().put(course.getCode(), course);
    }

    public boolean deleteByCode(String code) {
        return store.getCoursesByCode().remove(code) != null;
    }

    public List<Course> findAllSortedByCode() {
        List<Course> list = new ArrayList<>(store.getCoursesByCode().values());
        list.sort(Comparator.comparing(Course::getCode));
        return list;
    }
}
