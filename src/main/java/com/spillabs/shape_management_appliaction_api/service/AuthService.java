package com.spillabs.shape_management_appliaction_api.service;

import com.spillabs.shape_management_appliaction_api.dto.AuthRequest;
import com.spillabs.shape_management_appliaction_api.dto.AuthResponse;
import com.spillabs.shape_management_appliaction_api.dto.RegisterRequest;
import com.spillabs.shape_management_appliaction_api.dto.UserProfileResponse;
import com.spillabs.shape_management_appliaction_api.exception.UserAlreadyExistsException;
import com.spillabs.shape_management_appliaction_api.model.User;
import com.spillabs.shape_management_appliaction_api.repository.UserRepository;
import com.spillabs.shape_management_appliaction_api.security.JwtService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class AuthService {

    private final UserRepository userRepo;
    private final PasswordEncoder passwordEncoder;
    private final AuthenticationManager authenticationManager;
    private final JwtService jwtService;

    public AuthResponse register(RegisterRequest request) {
        if (userRepo.existsByUsername(request.getUsername())) {
            throw new UserAlreadyExistsException("Username already exists");
        }

        User user = User.builder()
                .username(request.getUsername())
                .password(passwordEncoder.encode(request.getPassword()))
                .build();

        userRepo.save(user);
        String token = jwtService.generateToken(user.getUsername());
        return new AuthResponse(token);
    }

    public AuthResponse authenticate(AuthRequest request) {
        try {
            authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(
                            request.getUsername(),
                            request.getPassword()
                    )
            );
        } catch (BadCredentialsException e) {
            throw new BadCredentialsException("Invalid username or password");
        }
        String token = jwtService.generateToken(request.getUsername());
        return new AuthResponse(token);
    }

    public UserProfileResponse getCurrentUser() {
        String username = SecurityContextHolder.getContext().getAuthentication().getName();

        User user = userRepo.findByUsername(username)
                .orElseThrow(() -> new UsernameNotFoundException("User not found"));

        return new UserProfileResponse(user.getId(), user.getUsername());
    }
}
