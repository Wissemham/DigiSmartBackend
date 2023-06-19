package com.stage.digibackend.services;

import java.util.List;

import com.stage.digibackend.Collections.User;

public interface IUserservice {
    String addUser(User user);
    List<User> getAllUsers();
    User getUserById(String userId);
    User updateUser(String userId,User userRequest);
    String deleteUser(String userId);
    List<User> ListAdmin();

    List<User> ListAllClient() ;
    List<User> ListClient(String admin);
    void resetPassword(String email);
    String verifiePwd(String code, String pwd);
}

