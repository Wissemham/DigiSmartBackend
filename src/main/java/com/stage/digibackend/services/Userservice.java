package com.stage.digibackend.services;

import com.stage.digibackend.Collections.User;
import com.stage.digibackend.Configuration.TunisieSmsConfig;
import com.stage.digibackend.dto.OtpStatus;
import com.stage.digibackend.dto.PasswordResetResponse;
import com.stage.digibackend.repository.UserRepository;
import org.apache.commons.lang3.RandomStringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;


import javax.mail.MessagingException;
import javax.mail.internet.MimeMessage;
import java.io.UnsupportedEncodingException;
import java.net.URL;
import java.net.URLConnection;
import java.util.List;

@Service
public class Userservice implements IUserservice {
    @Autowired
    UserRepository userRepository;
    @Autowired
    private PasswordEncoder passwordEncoder;
    @Autowired
    private TunisieSmsConfig tunisiesmsConfig;
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
    public User getUserBytelephone(String telephone) {
        return userRepository.findByTelephone(telephone);
    }

    @Override
    public User updateUser(String userId,User userRequest) {
        //get the document from db with the specific id
        User existingUser= userRepository.findById(userId).get();
        existingUser.setTelephone(userRequest.getTelephone());
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
        return   userRepository.findByRoleNot("6489c4d71478ef0f49b46b90");
    }


    @Override
    public List<User> ListAllClient() {
        return userRepository.findByRoleNot("648a2d798fce3961ef16dc4e");
    }

    @Override
    public List<User> ListClient(String admin) {
        return userRepository.findByAdmin(admin);
    }


    @Override
    public void resetPassword(String email) throws MessagingException, UnsupportedEncodingException {
        User user = userRepository.getUserByUsername(email);
        String randomCode = RandomStringUtils.random(6, true, true);
        user.setVerify(randomCode);
        userRepository.save(user);
        String toAddress = "yacinbnsalh@gmail.com";
        String fromAddress = "aladin.hammouda@esprit.tn";
        String senderName = "Digi-Smart-Solution";
        String subject = "Your verify code:";
        String content = "Dear [[name]],<br>"
                + "This your verify password code:<br>"
                + randomCode +"<br>"
                + "Thank you,<br>"
                + "Digi-Smart-Solution.";
        System.out.println("send");
        MimeMessage message = mailSender.createMimeMessage();
        MimeMessageHelper helper = new MimeMessageHelper(message);

        helper.setFrom(fromAddress, senderName);
        helper.setTo(toAddress);
        helper.setSubject(subject);

        content = content.replace("[[name]]", user.getUsername());
      //  String verifyURL = siteURL + "/verify?code=" + user.getVerificationCode();

      //  content = content.replace("[[URL]]", verifyURL);

        helper.setText(content, true);

        mailSender.send(message);
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
    @Override
    public PasswordResetResponse sendOTPForPasswordResest(String telephone) {
        PasswordResetResponse response=null;
        try {

            String mySender = tunisiesmsConfig.getSender();
            String myKey= tunisiesmsConfig.getKey();
            String randomCode = RandomStringUtils.random(6, true, true);
            User user = getUserBytelephone(telephone);
            System.out.println(user);
            if(user!=null)
            {
                user.setVerify(randomCode);
                userRepository.save(user);
                String otpMessage="OTP:"+randomCode;
                String Url_str = "https://www.tunisiesms.tn/client/Api/Api.aspx?fct=sms&key=MYKEY&mobile=216XXXXXXXX&sms=Hello+World&sender=YYYYYYYY";
                Url_str = Url_str.replace("MYKEY", myKey);
                Url_str = Url_str.replace("216XXXXXXXX", "216"+telephone);
                Url_str = Url_str.replace("Hello+World", otpMessage);
                Url_str = Url_str.replace("YYYYYYYY", mySender);
                URL myURL = new URL(Url_str);
                URLConnection myURLConnection = myURL.openConnection();
                myURLConnection.connect();
                response=new PasswordResetResponse(OtpStatus.DELIVERED,Url_str);

            }
            else{
                return new PasswordResetResponse(OtpStatus.FAILED,"Check your phone number");
            }

        } catch (Exception exp) {
            response=new PasswordResetResponse(OtpStatus.FAILED,exp.getMessage());}

        return response;

    }



}
