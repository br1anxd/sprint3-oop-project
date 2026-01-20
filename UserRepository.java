package com.umt.sprint2.data.repo;

import com.umt.sprint2.data.DataStore;
import com.umt.sprint2.model.User;

import java.util.Optional;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

public class UserRepository {
    private final DataStore store;

    public UserRepository(DataStore store) {
        this.store = store;
    }

    public Optional<User> findByUsername(String username) {
        return Optional.ofNullable(store.getUsersByUsername().get(username));
    }

    public void upsert(User user) {
        store.getUsersByUsername().put(user.getUsername(), user);
    }

    public boolean deleteByUsername(String username) {
        return store.getUsersByUsername().remove(username) != null;
    }

    public List<User> findAllSortedByUsername() {
        List<User> list = new ArrayList<>(store.getUsersByUsername().values());
        list.sort(Comparator.comparing(User::getUsername));
        return list;
    }
}
