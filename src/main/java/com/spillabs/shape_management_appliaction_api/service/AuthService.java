package com.spillabs.shape_management_appliaction_api.service;

import com.spillabs.shape_management_appliaction_api.dto.JwtResponse;
import com.spillabs.shape_management_appliaction_api.dto.LoginRequest;
import com.spillabs.shape_management_appliaction_api.dto.MessageResponse;
import com.spillabs.shape_management_appliaction_api.dto.SignupRequest;
import com.spillabs.shape_management_appliaction_api.entity.RefreshToken;
import com.spillabs.shape_management_appliaction_api.entity.Role;
import com.spillabs.shape_management_appliaction_api.entity.User;
import com.spillabs.shape_management_appliaction_api.exception.TokenRefreshException;
import com.spillabs.shape_management_appliaction_api.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class AuthService {

    @Autowired
    private AuthenticationManager authenticationManager;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private JwtService jwtService;

    @Autowired
    private RefreshTokenService refreshTokenService;

    public JwtResponse authenticateUser(LoginRequest loginRequest) {
        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(
                        loginRequest.getUsername(),
                        loginRequest.getPassword()
                )
        );

        SecurityContextHolder.getContext().setAuthentication(authentication);

        User userPrincipal = (User) authentication.getPrincipal();
        String accessToken = jwtService.generateAccessToken(userPrincipal);
        RefreshToken refreshToken = refreshTokenService.createRefreshToken(userPrincipal);

        return new JwtResponse(
                accessToken,
                refreshToken.getToken(),
                "Bearer",
                userPrincipal.getId(),
                userPrincipal.getUsername(),
                userPrincipal.getFirstName(),
                userPrincipal.getLastName(),
                userPrincipal.getRole().name()
        );
    }

    @Transactional
    public MessageResponse registerUser(SignupRequest signupRequest) {
        if (userRepository.existsByUsername(signupRequest.getUsername())) {
            throw new RuntimeException("Error: Username is already taken!");
        }

        User user = new User(
                signupRequest.getUsername(),
                passwordEncoder.encode(signupRequest.getPassword()),
                signupRequest.getFirstName(),
                signupRequest.getLastName()
        );

        user.setRole(Role.USER);
        userRepository.save(user);

        return new MessageResponse("User registered successfully!");
    }

    public JwtResponse refreshToken(String refreshToken) {
        return refreshTokenService.findByToken(refreshToken)
                .map(refreshTokenService::verifyExpiration)
                .map(RefreshToken::getUser)
                .map(user -> {
                    String newAccessToken = jwtService.generateAccessToken(user);
                    return new JwtResponse(
                            newAccessToken,
                            refreshToken,
                            "Bearer",
                            user.getId(),
                            user.getUsername(),
                            user.getFirstName(),
                            user.getLastName(),
                            user.getRole().name()
                    );
                })
                .orElseThrow(() -> new TokenRefreshException("Refresh token is not in database!"));
    }

    @Transactional
    public MessageResponse logoutUser() {
        User user = (User) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        refreshTokenService.deleteByUser(user);
        return new MessageResponse("Log out successful!");
    }
}
