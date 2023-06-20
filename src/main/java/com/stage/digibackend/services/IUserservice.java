package com.stage.digibackend.services;

import java.io.UnsupportedEncodingException;
import java.util.List;

import com.stage.digibackend.Collections.User;

import javax.mail.MessagingException;

public interface IUserservice {
    String addUser(User user);
    List<User> getAllUsers();
    User getUserById(String userId);
    User updateUser(String userId,User userRequest);
    String deleteUser(String userId);
    List<User> ListAdmin();

    List<User> ListAllClient() ;
    List<User> ListClient(String admin);
    void resetPassword(String email) throws MessagingException, UnsupportedEncodingException;
    String verifiePwd(String code, String pwd);
}

