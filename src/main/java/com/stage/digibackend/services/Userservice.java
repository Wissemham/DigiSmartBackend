package com.stage.digibackend.services;

import com.stage.digibackend.Collections.User;
import com.stage.digibackend.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import sun.reflect.generics.tree.BooleanSignature;

import javax.mail.MessagingException;
import java.io.UnsupportedEncodingException;
import java.util.List;

@Service
public class Userservice implements IUserservice {
    @Autowired
    UserRepository userRepository;
    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private JavaMailSender mailSender;






    //CRUD on user Crete , Read, Update, Delete
    @Override
    public String addUser(User user) {
        return userRepository.save(user).getId();
    }
    @Override
    public List<User> getAllUsers() {
        return userRepository.findAll();
    }
    @Override
    public User getUserById(String userId) {
        return userRepository.findById(userId).get();
    }

    @Override
    public User updateUser(String userId,User userRequest) {
        //get the document from db with the specific id
        User existingUser= userRepository.findById(userId).get();
        existingUser.setEmail(userRequest.getEmail());
        existingUser.setRoles(userRequest.getRoles());
        existingUser.setPassword(userRequest.getPassword());
        return userRepository.save(existingUser);
    }
    @Override
    public String deleteUser(String userId) {
        userRepository.deleteById(userId);
        return userId+"User deleted succesully";
    }

}
