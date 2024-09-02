package com.example.UserServiceAPI.security;

import com.example.UserServiceAPI.Models.User;
import com.example.UserServiceAPI.Repository.UserRepository;
import com.example.UserServiceAPI.UserServiceApiApplication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class customSpringUserDetailsService implements UserDetailsService {

    private UserRepository userRepository;

    public customSpringUserDetailsService(UserRepository userRepository){
        this.userRepository = userRepository;
    }
    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        Optional<User> userOptional = userRepository.findByEmail(username);

        if(userOptional.isEmpty()){
            throw new UsernameNotFoundException("User does not exist");
        }

        User user = userOptional.get();
        return new CustomSpringUserDetails(user);
    }
}
