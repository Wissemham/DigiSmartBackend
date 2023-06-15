package com.stage.digibackend.controllers;

import com.stage.digibackend.Collections.User;
import com.stage.digibackend.services.IUserservice;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;
@RestController
@RequestMapping("/users")

@CrossOrigin(origins = "*")
public class UserController {
    @Autowired
    IUserservice iUserService;
    //Add a user

    @PostMapping("/adduser")
    public String addUser(@RequestBody User user)
    {
        return iUserService.addUser(user);
    }
    //get all users
    @PreAuthorize("hasAnyAuthority('SUPER_ADMIN')")
    @GetMapping("/get")
    public List<User> getUsers()
    {
        return iUserService.getAllUsers();
    }
    //get user by id
    @PreAuthorize("hasAnyAuthority('SUPER_ADMIN')")
    @GetMapping("/{userId}")
    public User getUser(@PathVariable String userId)
    {
        return iUserService.getUserById(userId);
    }
    @PreAuthorize("hasAnyAuthority('SUPER_ADMIN')")
    @PutMapping("/updateuser/{userId}")
    public User modifyUser(@PathVariable String userId,@RequestBody User user) {
        return iUserService.updateUser(userId,user);
    }
    @PreAuthorize("hasAnyAuthority('SUPER_ADMIN')")
    @DeleteMapping("/deleteuser/{userId}")
    public String deleteUser(@PathVariable String userId)
    {
        return iUserService.deleteUser(userId);    }
////Liste des admin
    @GetMapping("/ListAdmin")
    List<User> ListAdmin(){
        return iUserService.ListAdmin();
    }
/////Liste de client pour chaque admin
    @GetMapping("/ListClientByAdmin/{user}")
    List<User> ListClient(@PathVariable String user){
        return iUserService.ListClient(user);
    }

    ///reset password
    @PutMapping("/resetPwd/{email}")
    void verifypwd(@PathVariable String email){
         iUserService.resetPassword(email);
    }

    @GetMapping("/verifiePwd/{code}/{pwd}")
    String verifiePwd(@PathVariable String code,@PathVariable String pwd){
        return iUserService.verifiePwd(code,pwd);
    }


}
