package com.ananya.transport.dto;

import com.ananya.transport.entity.*;
import jakarta.validation.constraints.*;
import java.time.LocalDateTime;

public final class AdminDtos {
    private AdminDtos() {}
    public record CreateUserRequest(@NotBlank String name, @Email @NotBlank String email,
                                    @Size(min=8) String password, @NotNull Role role) {}
    public record VehicleRequest(@NotBlank String registrationNumber, @NotBlank String model,
                                 @Min(1) @Max(100) int capacity) {}
    public record RouteRequest(@NotBlank String name, @NotBlank String pickupPoint,
                               @NotBlank String dropPoint, @Positive double distanceKm) {}
    public record TripRequest(@NotNull Long routeId, @NotNull Long vehicleId,
                              @NotNull Long driverId, @Future LocalDateTime departureTime) {}
    public record TripStatusRequest(@NotNull TripStatus status) {}
}
