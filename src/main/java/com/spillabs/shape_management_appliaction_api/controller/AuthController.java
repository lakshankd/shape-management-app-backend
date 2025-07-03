package com.spillabs.shape_management_appliaction_api.controller;


import com.spillabs.shape_management_appliaction_api.dto.AuthRequest;
import com.spillabs.shape_management_appliaction_api.dto.AuthResponse;
import com.spillabs.shape_management_appliaction_api.dto.RegisterRequest;
import com.spillabs.shape_management_appliaction_api.dto.UserProfileResponse;
import com.spillabs.shape_management_appliaction_api.service.AuthService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/auth")
@RequiredArgsConstructor
public class AuthController {

    private final AuthService authService;

    @PostMapping("/register")
    public ResponseEntity<AuthResponse> register(@RequestBody RegisterRequest request) {
        return ResponseEntity.ok(authService.register(request));
    }

    @PostMapping("/login")
    public ResponseEntity<AuthResponse> login(@RequestBody AuthRequest request) {
        return ResponseEntity.ok(authService.authenticate(request));
    }

    @GetMapping("/me")
    public ResponseEntity<UserProfileResponse> getCurrentUser() {
        return ResponseEntity.ok(authService.getCurrentUser());
    }
}
