package com.ecommerce.auth.service;

import com.ecommerce.auth.dto.AuthResponse;
import com.ecommerce.auth.dto.LoginRequest;
import com.ecommerce.auth.dto.RegisterRequest;
import com.ecommerce.auth.entity.Role;
import com.ecommerce.auth.entity.User;
import com.ecommerce.auth.exception.CustomException;
import com.ecommerce.auth.repository.UserRepository;

import org.springframework.http.HttpStatus;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
public class AuthService {
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;

    public AuthService(UserRepository userRepository, PasswordEncoder passwordEncoder, JwtService jwtService) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        this.jwtService = jwtService;
    }
    public AuthResponse registerUser(RegisterRequest request){
        if (userRepository.existsByEmail(request.getEmail())) {
            throw new RuntimeException(
                    "Email already registered"
            );
        }
        User user = new User();
        user.setName(request.getName());
        user.setEmail(request.getEmail());
        // Never store raw passwords
        user.setPassword(
                passwordEncoder.encode(request.getPassword())
        );
//-------------------------------{
//     "name": "Admin",
//     "email": "admin@gmail.com",
//     "password": "Admin@123"
// }----------------------------
        // Every normal registration is a USER
        user.setRole(Role.ROLE_USER);

        User savedUser = userRepository.save(user);
        String token = jwtService.generateToken(
                savedUser.getId(),
                savedUser.getEmail(),
                savedUser.getRole().name()
        );

        return new AuthResponse(
                savedUser.getId(),
                savedUser.getName(),
                savedUser.getEmail(),
                savedUser.getRole().name() ,
                token
        );
        
    }

    public AuthResponse login(LoginRequest request) {

    User user = userRepository
            .findByEmail(request.getEmail())
            .orElseThrow(() ->
                    new CustomException(HttpStatus.NOT_FOUND,
                            "Invalid email or password"
                    ));

    if (!passwordEncoder.matches(
            request.getPassword(),
            user.getPassword())) {

        throw new CustomException(
                HttpStatus.UNAUTHORIZED,
                "Invalid email or password"
        );
    }

    String token = jwtService.generateToken(
        user.getId(),
            user.getEmail(),
            user.getRole().name()
    );

    return new AuthResponse(
            user.getId(),
            user.getName(),
            user.getEmail(),
            user.getRole().name(),
            token
    );
}


}
