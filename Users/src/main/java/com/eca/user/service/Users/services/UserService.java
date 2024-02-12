package com.eca.user.service.Users.services;

import java.util.List;
import com.eca.user.service.Users.entities.User;

public interface UserService {
    User saveUser(User user);
    List<User> getAllUsers();
    User getUser(String userId);
}
