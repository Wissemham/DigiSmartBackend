package com.stage.digibackend.controllers;

import com.fasterxml.jackson.databind.util.JSONPObject;
import com.stage.digibackend.Collections.ERole;
import com.stage.digibackend.Collections.Role;
import com.stage.digibackend.Collections.User;
import com.stage.digibackend.payload.request.LoginRequest;
import com.stage.digibackend.payload.request.SignupRequest;
import com.stage.digibackend.payload.response.JwtResponse;
import com.stage.digibackend.payload.response.MessageResponse;
import com.stage.digibackend.repository.RoleRepository;
import com.stage.digibackend.repository.UserRepository;
import com.stage.digibackend.security.jwt.JwtUtils;
import com.stage.digibackend.security.services.TokenService;
import com.stage.digibackend.security.services.UserDetailsImpl;
import com.stage.digibackend.services.Userservice;
import org.apache.commons.lang3.RandomStringUtils;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;
import org.springframework.beans.factory.DisposableBean;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.session.SessionInformation;
import org.springframework.security.core.session.SessionRegistry;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.RestTemplate;

import javax.mail.MessagingException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import javax.validation.Valid;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.InetAddress;
import java.net.URL;
import java.net.UnknownHostException;
import java.util.*;
import javax.validation.Valid;
import java.io.UnsupportedEncodingException;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@CrossOrigin(origins = "*", maxAge = 3600)
@RestController
@RequestMapping("/api/auth")
public class AuthController implements DisposableBean, InitializingBean {
	@Autowired
	AuthenticationManager authenticationManager;

	@Autowired
	UserRepository userRepository;

	@Autowired
	RoleRepository roleRepository;

	@Autowired
	PasswordEncoder encoder;

	@Autowired
	JwtUtils jwtUtils;

	@Autowired
	SessionRegistry sessionRegistry;
	@Autowired
	private PasswordEncoder passwordEncoder;

	@Autowired
	private JavaMailSender mailSender;
	@Autowired
	private Userservice userservice;
	@Autowired
	private HttpServletRequest request;


	private  TokenService tokenService;

	private Map<String, Integer> sessionCountMap = new HashMap<>();



	@PostMapping("/signin")
	public ResponseEntity<?> authenticateUser(@Valid @RequestBody LoginRequest loginRequest, HttpSession session) {

		User u = userRepository.findByEmail(loginRequest.getEmail()).get();
		if(u==null){
			return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Invalid email");
		}else if (u.getVerificationCode()!=null){
			return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Verify your account");
		}
		Authentication authentication = authenticationManager.authenticate(
				new UsernamePasswordAuthenticationToken(loginRequest.getEmail(), loginRequest.getPassword()));

		SecurityContextHolder.getContext().setAuthentication(authentication);
		String jwt = jwtUtils.generateJwtToken(authentication);
		String ipAddress = getIpAddress();
		System.out.println(ipAddress);
		String email = loginRequest.getEmail();
		Integer numSessions = sessionCountMap.get(email);
		if (numSessions == null) {
			numSessions = 1;
		} else if (numSessions >= 3) {
			// Maximum of three sessions reached
			return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Maximum number of sessions reached for this user.");
		} else {
			numSessions++;
		}
		sessionCountMap.put(email, numSessions);

		UserDetailsImpl userDetails = (UserDetailsImpl) authentication.getPrincipal();
		List<String> roles = userDetails.getAuthorities().stream()
				.map(item -> item.getAuthority())
				.collect(Collectors.toList());

		//String userIpAddress = getUserIpAddress(loginRequest.getEmail());
		String userLocation = getUserLocation(request);
		System.out.println("/"+userLocation);
		return ResponseEntity.ok(new JwtResponse(jwt,
				userDetails.getId(),
				userDetails.getUsername(),
				userDetails.getEmail(),
				roles,
				userLocation));

	}
	private String getIpAddress() {
		try {
			InetAddress inetAddress = InetAddress.getLocalHost();
			System.out.println("******"+inetAddress.getHostName());
			return inetAddress.getHostAddress();
		} catch (UnknownHostException e) {
			e.printStackTrace();
			return null;
		}

	}
	/*@Autowired
	private PasswordEncoder passwordEncoder;


		/*Authentication authentication = authenticationManager.authenticate(
				new UsernamePasswordAuthenticationToken(loginRequest.getEmail(), loginRequest.getPassword()));

		SecurityContextHolder.getContext().setAuthentication(authentication);
		String jwt = jwtUtils.generateJwtToken(authentication);
		Integer numBrowsers = (Integer) session.getAttribute(loginRequest.getEmail());
		if (numBrowsers == null) {
			numBrowsers = 1;
		} else if (numBrowsers >= 3) {
			// Maximum of three browsers reached
			return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Maximum number of browsers reached for this user.");
		} else {
			numBrowsers++;
		}
		session.setAttribute(loginRequest.getEmail(), numBrowsers);
		System.out.println(numBrowsers);
		UserDetailsImpl userDetails = (UserDetailsImpl) authentication.getPrincipal();
		List<String> roles = userDetails.getAuthorities().stream()
				.map(item -> item.getAuthority())
				.collect(Collectors.toList());
		return ResponseEntity.ok(new JwtResponse(jwt,
				userDetails.getId(),
				userDetails.getUsername(),
				userDetails.getEmail(),
				roles));
	}*/










	@PostMapping("/signup/{id}")
	public ResponseEntity<?> registerUser(@Valid @RequestBody SignupRequest signUpRequest,String siteURL,@PathVariable("id") String idUser) throws MessagingException, UnsupportedEncodingException {
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

		Set<String> strRoles = signUpRequest.getRoles();
		Set<Role> roles = new HashSet<>();

		if (strRoles == null) {
			Role userRole = roleRepository.findByName(ERole.SUPER_ADMIN)
					.orElseThrow(() -> new RuntimeException("Error: Role is not found."));
			roles.add(userRole);
		} else {
			strRoles.forEach(role -> {
				switch (role) {
				case "admin":
					Role adminRole = roleRepository.findByName(ERole.ADMIN)
							.orElseThrow(() -> new RuntimeException("Error: Role is not found."));
					roles.add(adminRole);

					break;
				case "client":
					Role modRole = roleRepository.findByName(ERole.CLIENT)
							.orElseThrow(() -> new RuntimeException("Error: Role is not found."));
					roles.add(modRole);

					break;
				default:
					Role userRole = roleRepository.findByName(ERole.SUPER_ADMIN)
							.orElseThrow(() -> new RuntimeException("Error: Role is not found."));
					roles.add(userRole);
				}
			});
		}
		User admin = userRepository.findById(idUser).get();
		user.setRoles(roles);
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
	public ResponseEntity<?> logoutUser(HttpServletRequest request) {
		// Get the user's email from the SecurityContext
		Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
		String userEmail = ((UserDetailsImpl) authentication.getPrincipal()).getEmail();

		// Invalidate the session and decrement the number of sessions
		HttpSession session = request.getSession(false);
		if (session != null) {
			Integer numSessions = (Integer) session.getAttribute(userEmail);
			if (numSessions != null && numSessions > 0) {
				numSessions--;
				session.setAttribute(userEmail, numSessions);
				System.out.println("Number of sessions for " + userEmail + ": " + numSessions);
			}
			session.invalidate();
		}

		return ResponseEntity.ok(new MessageResponse("Logout successful!"));
	}

	@GetMapping ("/destroy")
	public String dst() throws Exception {
		destroy();
		return "ok";
	}
	@Override
	public void destroy() throws Exception {
		// Decrement the session count for the current user when the session is destroyed
		Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
		if (authentication != null && authentication.getPrincipal() instanceof UserDetailsImpl) {
			UserDetailsImpl userDetails = (UserDetailsImpl) authentication.getPrincipal();
			String email = userDetails.getEmail();
			Integer numSessions = sessionCountMap.get(email);
			if (numSessions != null) {
				numSessions--;
				sessionCountMap.put(email, numSessions);
			}
		}

	}
	//private String getUserLocation(String userEmail) {

	private String getUserLocation(HttpServletRequest request) {
		String userIpAddress = request.getRemoteAddr();
		String apiUrl = "http://ip-api.com/json/" + userIpAddress;
		RestTemplate restTemplate = new RestTemplate();
		String response = restTemplate.getForObject(apiUrl, String.class);
		String userLocation = "Unknown";

		try {
			JSONParser parser = new JSONParser();
			JSONObject json = (JSONObject) parser.parse(response);
			String city = (String) json.get("city");
			String country = (String) json.get("country");

			if (city != null && country != null) {
				userLocation = city + ", " + country;
			}
		} catch (ParseException e) {
			e.printStackTrace();
		}

		return userLocation;
	}

		// Retrieve the user's IP address based on the provided email
		/*String userIpAddress = getUserIpAddress(userEmail);

		// Make a REST API call to the IP-API service to get the location information
		String apiUrl = "http://ip-api.com/json/" + userIpAddress;
		RestTemplate restTemplate = new RestTemplate();
		String response = restTemplate.getForObject(apiUrl, String.class);

		String userLocation = "Unknown";
		try {
			JSONParser parser = new JSONParser();
			JSONObject json = (JSONObject) parser.parse(response);
			String city = (String) json.get("city");
			String country = (String) json.get("country");

			if (city != null && country != null) {
				userLocation = city + ", " + country;
			}
		} catch (ParseException e) {
			e.printStackTrace();
		}

		return userLocation;
	}*/
	private String getUserIpAddress(String userEmail) {
		// Retrieve the user's IP address from the request headers based on the provided email
		Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
		if (authentication != null && authentication.getPrincipal() instanceof UserDetailsImpl) {
			UserDetailsImpl userDetails = (UserDetailsImpl) authentication.getPrincipal();
			if (userDetails.getEmail().equals(userEmail)) {
				return request.getRemoteAddr();
			}
		}
		return null; // Handle the case when the user is not found or the email doesn't match
	}

	@Override
	public void afterPropertiesSet() throws Exception {

	}


}
