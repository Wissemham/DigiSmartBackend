package com.stage.digibackend.services;

import com.stage.digibackend.Collections.User;

import java.util.List;

public interface IUserservice {
    String addUser(User user);
    List<User> getAllUsers();
    User getUserById(String userId);
    User updateUser(String userId,User userRequest);
    String deleteUser(String userId);
    List<User> ListAdmin();
    List<User> ListClient(String admin);
    void resetPassword(String email);
    String verifiePwd(String code, String pwd);

}

