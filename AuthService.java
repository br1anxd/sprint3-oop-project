package com.umt.sprint2.logic;

import com.umt.sprint2.data.repo.UserRepository;
import com.umt.sprint2.model.User;

public class AuthService {
    private final UserRepository userRepo;

    public AuthService(UserRepository userRepo) {
        this.userRepo = userRepo;
    }

    public User login(String username, String password) throws AuthException {
        User user = userRepo.findByUsername(username)
                .orElseThrow(() -> new AuthException("Unknown username"));
        if (!user.passwordMatches(password)) {
            throw new AuthException("Invalid password");
        }
        return user;
    }
}
