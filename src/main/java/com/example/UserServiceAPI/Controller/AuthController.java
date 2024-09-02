package com.example.UserServiceAPI.Controller;

import com.example.UserServiceAPI.DTOs.LoginRequestDto;
import com.example.UserServiceAPI.DTOs.SignUpRequestDto;
import com.example.UserServiceAPI.DTOs.UserDto;
import com.example.UserServiceAPI.DTOs.ValidateTokenRequestDto;
import com.example.UserServiceAPI.Models.SessionStatus;
import com.example.UserServiceAPI.Models.User;
import com.example.UserServiceAPI.Service.AuthService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/auth")
public class AuthController {

    private AuthService authService;

    public AuthController(AuthService authService) {
        this.authService = authService;
    }

   /*@PostMapping("/login")
    public ResponseEntity<UserDto> login(@RequestBody LoginRequestDto request) {
        return authService.login(request.getEmail(), request.getPassword());
    }*/

    @PostMapping("/signup")
    public ResponseEntity<UserDto> signUp(@RequestBody SignUpRequestDto request) {
        UserDto userDto = authService.signUp(request.getEmail(), request.getPassword());
        return new ResponseEntity<>(userDto, HttpStatus.OK);
    }

    @PostMapping("/validate")
    public ResponseEntity<SessionStatus> validate(ValidateTokenRequestDto requestDto){
        SessionStatus sessionStatus = authService.validate(requestDto.getToken(),requestDto.getUserId());
        return new ResponseEntity<>(sessionStatus,HttpStatus.OK);
    }
}
