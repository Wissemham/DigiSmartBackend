package com.stage.digibackend.services;

import com.stage.digibackend.Collections.User;

import java.util.List;

public interface IUserservice {
    String addUser(User user);
    List<User> getAllUsers();
    User getUserById(String userId);
    User updateUser(String userId,User userRequest);
    String deleteUser(String userId);
}

