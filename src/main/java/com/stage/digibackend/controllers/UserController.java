package com.stage.digibackend.controllers;

import com.stage.digibackend.Collections.ERole;
import com.stage.digibackend.Collections.Role;
import com.stage.digibackend.Collections.User;
import com.stage.digibackend.payload.request.SignupRequest;
import com.stage.digibackend.payload.request.UserRequest;
import com.stage.digibackend.payload.response.MessageResponse;
import com.stage.digibackend.repository.RoleRepository;
import com.stage.digibackend.repository.UserRepository;
import com.stage.digibackend.services.IUserservice;
import com.stage.digibackend.services.Userservice;
import org.apache.commons.lang3.RandomStringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

import javax.mail.MessagingException;
import javax.validation.Valid;
import java.io.UnsupportedEncodingException;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

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


    @Autowired
    private PasswordEncoder passwordEncoder;
    @Autowired
    PasswordEncoder encoder;


    @Autowired
    private JavaMailSender mailSender;
    @Autowired
    private Userservice userservice;
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private RoleRepository roleRepository;

    @PreAuthorize("hasAuthority('SUPER_ADMIN')")
    @PostMapping("/AddAdmin")
    public ResponseEntity<?> registerUser(@Valid @RequestBody UserRequest signUpRequest, String siteURL) throws MessagingException, UnsupportedEncodingException {
        if (userRepository.existsByUsername(signUpRequest.getUsername())) {
            return ResponseEntity
                    .badRequest()
                    .body(new MessageResponse("Error: Username is already taken!"));
        }

        if (userRepository.existsByEmail(signUpRequest.getEmail())) {
            return ResponseEntity
                    .badRequest()
                    .body(new MessageResponse("Error: Email is already in use!"));
        }

        // Create new user's account
        User user = new User(signUpRequest.getUsername(),
                signUpRequest.getEmail(),
                encoder.encode(signUpRequest.getPassword()));


        Set<Role> roles = new HashSet<>();
        Role r =  roleRepository.findByName(ERole.ADMIN).get();
                if(roles==null){
                    return ResponseEntity.badRequest().body("Role not found");
                }

                roles.add(r);
        user.setRoles( roles);
        //userRepository.save(user);
        String encodedPassword = passwordEncoder.encode(user.getPassword());
        user.setPassword(encodedPassword);

        String randomCode = RandomStringUtils.random(64, true, true);
        user.setVerificationCode(randomCode);
        user.setEnabled(false);

        userRepository.save(user);


        System.out.println("registre");
        userservice.sendVerificationEmail(user, siteURL);
        return ResponseEntity.ok(new MessageResponse("User registered successfully!"));
    }

    @PostMapping("/AddClient/{id}")
    public ResponseEntity<?> registerClient(@Valid @RequestBody SignupRequest signUpRequest, String siteURL, @PathVariable("id") String idUser) throws MessagingException, UnsupportedEncodingException {
        if (userRepository.existsByUsername(signUpRequest.getUsername())) {
            return ResponseEntity
                    .badRequest()
                    .body(new MessageResponse("Error: Username is already taken!"));
        }

        if (userRepository.existsByEmail(signUpRequest.getEmail())) {
            return ResponseEntity
                    .badRequest()
                    .body(new MessageResponse("Error: Email is already in use!"));
        }

        // Create new user's account
        User user = new User(signUpRequest.getUsername(),
                signUpRequest.getEmail(),
                encoder.encode(signUpRequest.getPassword()));


        Set<Role> roles = new HashSet<>();
        Role r =  roleRepository.findByName(ERole.CLIENT).get();
        if(roles==null){
            return ResponseEntity.badRequest().body("Role not found");
        }
        User admin = userRepository.findById(idUser).get();
        roles.add(r);
        user.setRoles( roles);
        //userRepository.save(user);
        String encodedPassword = passwordEncoder.encode(user.getPassword());
        user.setPassword(encodedPassword);

        String randomCode = RandomStringUtils.random(64, true, true);
        user.setVerificationCode(randomCode);
        user.setEnabled(false);
        user.setAdmin(admin);
        userRepository.save(user);


        System.out.println("registre");
        userservice.sendVerificationEmail(user, siteURL);
        return ResponseEntity.ok(new MessageResponse("User registered successfully!"));
    }



}
