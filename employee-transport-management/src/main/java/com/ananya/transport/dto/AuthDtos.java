package com.ananya.transport.dto;

import com.ananya.transport.entity.Role;
import jakarta.validation.constraints.*;

public final class AuthDtos {
    private AuthDtos() {}
    public record RegisterRequest(@NotBlank String name, @Email @NotBlank String email,
                                  @Size(min=8, message="Password must contain at least 8 characters") String password) {}
    public record LoginRequest(@Email @NotBlank String email, @NotBlank String password) {}
    public record AuthResponse(String token, Long userId, String name, String email, Role role) {}
}
