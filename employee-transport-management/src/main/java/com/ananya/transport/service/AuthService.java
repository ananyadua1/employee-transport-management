package com.ananya.transport.service;
import com.ananya.transport.dto.AuthDtos.*;
import com.ananya.transport.entity.*;
import com.ananya.transport.exception.BusinessException;
import com.ananya.transport.repository.UserRepository;
import com.ananya.transport.security.JwtService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.*;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service @RequiredArgsConstructor
public class AuthService {
    private final UserRepository users; private final PasswordEncoder encoder; private final AuthenticationManager authManager; private final JwtService jwt;
    public AuthResponse register(RegisterRequest r) {
        if(users.existsByEmailIgnoreCase(r.email())) throw new BusinessException("Email is already registered");
        User u=users.save(User.builder().name(r.name().trim()).email(r.email().toLowerCase()).password(encoder.encode(r.password())).role(Role.EMPLOYEE).build());
        return response(u);
    }
    public AuthResponse login(LoginRequest r) {
        authManager.authenticate(new UsernamePasswordAuthenticationToken(r.email(),r.password()));
        User u=users.findByEmailIgnoreCase(r.email()).orElseThrow(); return response(u);
    }
    private AuthResponse response(User u) { return new AuthResponse(jwt.generateToken(u),u.getId(),u.getName(),u.getEmail(),u.getRole()); }
}
