package com.stage.digibackend.services;

import com.stage.digibackend.Collections.User;
import com.stage.digibackend.repository.UserRepository;
import org.apache.commons.lang3.RandomStringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import sun.reflect.generics.tree.BooleanSignature;

import javax.mail.MessagingException;
import javax.mail.internet.MimeMessage;
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



    public void register(User user, String siteURL) throws UnsupportedEncodingException, MessagingException {
        String encodedPassword = passwordEncoder.encode(user.getPassword());
        user.setPassword(encodedPassword);

        String randomCode = RandomStringUtils.random(64, true, true);
        user.setVerificationCode(randomCode);
        user.setEnabled(false);

        userRepository.save(user);
        System.out.println("registre");
        sendVerificationEmail(user, siteURL);
    }

    public void sendVerificationEmail(User user, String siteURL) throws MessagingException, UnsupportedEncodingException {
        String toAddress = "aladin.hammouda@esprit.tn";
        String fromAddress = "aladin.hammouda@esprit.tn";
        String senderName = "Digi-Smart-Solution";
        String subject = "Please verify your registration";
        String content = "Dear [[name]],<br>"
                + "Please click the link below to verify your registration:<br>"
                + "<h3><a href=\"[[URL]]\" target=\"_self\">VERIFY</a></h3>"
                + "Thank you,<br>"
                + "Digi-Smart-Solution.";
        System.out.println("send");
        MimeMessage message = mailSender.createMimeMessage();
        MimeMessageHelper helper = new MimeMessageHelper(message);

        helper.setFrom(fromAddress, senderName);
        helper.setTo(toAddress);
        helper.setSubject(subject);

        content = content.replace("[[name]]", user.getUsername());
        String verifyURL = siteURL + "/verify?code=" + user.getVerificationCode();

        content = content.replace("[[URL]]", verifyURL);

        helper.setText(content, true);

        mailSender.send(message);
    }


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

    @Override
    public List<User> ListAdmin() {
        return userRepository.findByRoleNot("6489c4d71478ef0f49b46b90");
    }

    @Override
    public List<User> ListClient(String admin) {
        return userRepository.findByAdmin(admin);
    }


    @Override
    public void resetPassword(String email) {
        User user = userRepository.getUserByUsername(email);
        String randomCode = RandomStringUtils.random(6, true, true);
        user.setVerify(randomCode);
        userRepository.save(user);
        //sendSms(randomCode);


    }

    @Override
    public String verifiePwd(String code, String pwd) {
        //User user = userRepository.getUserByVerifiepwd(code);
        //System.out.println(user.getIdUser());
        User user = userRepository.getUserCD(code);
        if(user!=null){
            user.setPassword(passwordEncoder.encode(pwd));
            user.setVerify(null);
            userRepository.save(user);
            return "valide";
        }
        return "Verifie your code";
    }


}
