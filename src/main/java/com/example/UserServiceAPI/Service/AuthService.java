package com.example.UserServiceAPI.Service;


import com.example.UserServiceAPI.DTOs.UserDto;
import com.example.UserServiceAPI.DTOs.ValidateTokenRequestDto;
import com.example.UserServiceAPI.Models.Role;
import com.example.UserServiceAPI.Models.Session;
import com.example.UserServiceAPI.Models.SessionStatus;
import com.example.UserServiceAPI.Models.User;
import com.example.UserServiceAPI.Repository.SessionRepository;
import com.example.UserServiceAPI.Repository.UserRepository;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jws;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.MacAlgorithm;
import org.apache.commons.lang3.RandomStringUtils;
import org.hibernate.query.UnknownSqlResultSetMappingException;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.util.MultiValueMapAdapter;

import javax.crypto.SecretKey;
import java.nio.charset.StandardCharsets;
import java.time.LocalDate;
import java.util.*;

@Service
public class AuthService {

    private UserRepository userRepository;
    private SessionRepository sessionRepository;

    private BCryptPasswordEncoder bCryptPasswordEncoder;

    public AuthService(UserRepository userRepository, SessionRepository sessionRepository, BCryptPasswordEncoder bCryptPasswordEncoder) {
        this.userRepository = userRepository;
        this.sessionRepository = sessionRepository;
        this.bCryptPasswordEncoder = bCryptPasswordEncoder;
    }

    public ResponseEntity<UserDto> login(String email, String password) {
        System.out.println("AuthService : Login()");
        Optional<User> userOptional = userRepository.findByEmail(email);

        if (userOptional.isEmpty()) {
            System.out.println("User is not found");
            return null;
        }

        User user = userOptional.get();

        if(!bCryptPasswordEncoder.matches(password,user.getPassword())){
            System.out.println("Password is incorrect");
            throw new RuntimeException("Password is incorrect");
            //return  null;
        }

        //Create a random token
        String token = RandomStringUtils.randomAlphanumeric(30);

        MacAlgorithm alg = Jwts.SIG.HS256;
        SecretKey key = alg.key().build();

        /*String message = "{\n" +
                "   \"email\": \"naman@scaler.com\",\n" +
                "   \"roles\": [\n" +
                "      \"mentor\",\n" +
                "      \"ta\"\n" +
                "   ],\n" +
                "   \"expirationDate\": \"23rdOctober2023\"\n" +
                "}";*/
//        // user_id
//        // user_email
//        // roles

        Map<String, Object> jsonForJwt = new HashMap<>();
        jsonForJwt.put("email",user.getEmail());
        jsonForJwt.put("role",user.getRoles());
        jsonForJwt.put("CreatedAt",new Date());
        jsonForJwt.put("expiryAt", LocalDate.now().plusDays(3).toEpochDay());

        //byte[] content = message.getBytes(StandardCharsets.UTF_8);
        token = Jwts.builder().claims(jsonForJwt).signWith(key,alg).compact();

        //content = Jwts.parser().verifyWith(key).build().parseSignedContent(jws).getPayload();
        //Store the token in the session table
        Session session = new Session();
        session.setSessionStatus(SessionStatus.ACTIVE);
        session.setUser(user);
        session.setToken(token);
        sessionRepository.save(session);

        UserDto userDto = UserDto.from(user);

        MultiValueMapAdapter<String,String> headers = new MultiValueMapAdapter<>(new HashMap<>());
        headers.add(HttpHeaders.SET_COOKIE,"auth-token"+token);

        ResponseEntity<UserDto> response = new ResponseEntity<>(userDto,headers, HttpStatus.OK);
//        response.getHeaders().add(HttpHeaders.SET_COOKIE, token);

        System.out.println("User is found");

        return response;
    }

    public UserDto signUp(String email, String password) {
        System.out.println("AuthService : SignUp()");
        User user = new User();
        user.setEmail(email);
        user.setPassword(bCryptPasswordEncoder.encode(password));
        User savedUser = userRepository.save(user);

        System.out.println("User "+user.getEmail()+ " has Signed up");

        UserDto userDto = UserDto.from(savedUser);

        return userDto;
    }

    public SessionStatus validate(String token, Long userId){
        System.out.println("AuthService : validate()");
        Optional<Session> sessionOptional = sessionRepository.findByTokenAndUser_Id(token,userId);

        if(sessionOptional.isEmpty()){
            System.out.println("Session is empty");
            return SessionStatus.ENDED;
        }

        Session session = sessionOptional.get();

        if(!session.getSessionStatus().equals(SessionStatus.ACTIVE)){
            System.out.println("Session has ENDED");
            return SessionStatus.ENDED;
        }

        token = session.getToken();

        Jws<Claims> claimsJwts =  Jwts.parser().build().parseSignedClaims(token);

        String email =(String) claimsJwts.getPayload().get("email");
        List<Role> roles = (List<Role>) claimsJwts.getPayload().get("roles");
        Date createdAt = (Date) claimsJwts.getPayload().get("createdAt");

        if(createdAt.before(new Date())){
            return SessionStatus.ENDED;
        }

        System.out.println("Session is ACTIVE");

        return SessionStatus.ACTIVE;

    }
}
