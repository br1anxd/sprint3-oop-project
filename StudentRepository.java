package com.umt.sprint2.data.repo;

import com.umt.sprint2.data.DataStore;
import com.umt.sprint2.model.Student;

import java.util.*;

public class StudentRepository {
    private final DataStore store;

    public StudentRepository(DataStore store) {
        this.store = store;
    }

    public Optional<Student> findById(String id) {
        return Optional.ofNullable(store.getStudentsById().get(id));
    }

    public void upsert(Student student) {
        store.getStudentsById().put(student.getId(), student);
    }

    public boolean deleteById(String id) {
        return store.getStudentsById().remove(id) != null;
    }

    public List<Student> findAllSortedById() {
        List<Student> list = new ArrayList<>(store.getStudentsById().values());
        list.sort(Comparator.comparing(Student::getId));
        return list;
    }
}
