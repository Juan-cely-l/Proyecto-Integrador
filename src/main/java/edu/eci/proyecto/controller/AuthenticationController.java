package edu.eci.proyecto.controller;

import edu.eci.proyecto.dto.AuthRequest;
import edu.eci.proyecto.dto.AuthResponse;
import edu.eci.proyecto.security.JwtUtil;
import edu.eci.proyecto.security.CustomUserDetailsService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/auth/login")
public class AuthenticationController {
    private final AuthenticationManager authenticationManager;
    private final  CustomUserDetailsService userDetailsService;
    private final JwtUtil jwtUtil;

    public AuthenticationController(AuthenticationManager authenticationManager, CustomUserDetailsService userDetailsService, JwtUtil jwtUtil) {
        this.authenticationManager = authenticationManager;
        this.userDetailsService = userDetailsService;
        this.jwtUtil = jwtUtil;
    }
    @PostMapping
    public ResponseEntity<AuthResponse>login(@Valid @RequestBody AuthRequest request){
        String email= request.email().toLowerCase();
        authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(email, request.password())
            );
            UserDetails userDetails=userDetailsService
                    .loadUserByUsername(email);
            String jwt=jwtUtil.generateToken(userDetails);
            return ResponseEntity.ok(new AuthResponse(jwt));
    }
}
