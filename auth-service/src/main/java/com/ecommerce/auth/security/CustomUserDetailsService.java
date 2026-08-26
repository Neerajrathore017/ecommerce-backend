package com.ecommerce.auth.security;

import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import com.ecommerce.auth.entity.User;
import com.ecommerce.auth.repository.UserRepository;

@Service
public class CustomUserDetailsService implements UserDetailsService {
    // Implement the methods required by UserDetailsService
    private final UserRepository userRepository;

    public CustomUserDetailsService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }
    @Override
    public UserDetails loadUserByUsername(String email) {
        // Load user from the database using the UserRepository
        // and return a UserDetails object
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new UsernameNotFoundException("User not found with email: " + email));
      return new org.springframework.security.core.userdetails.User(
                user.getEmail(),
                user.getPassword(),
                java.util.List.of(
                        new SimpleGrantedAuthority(
                                user.getRole().name()
                        )
                )
        );
    }
}
