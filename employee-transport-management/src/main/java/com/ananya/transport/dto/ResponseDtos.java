package com.ananya.transport.dto;

import com.ananya.transport.entity.*;
import java.time.LocalDateTime;

public final class ResponseDtos {
    private ResponseDtos() {}
    public record UserResponse(Long id, String name, String email, Role role, boolean enabled) {
        public static UserResponse from(User u) { return new UserResponse(u.getId(),u.getName(),u.getEmail(),u.getRole(),u.isEnabled()); }
    }
    public record VehicleResponse(Long id, String registrationNumber, String model, int capacity, boolean active) {
        public static VehicleResponse from(Vehicle v) { return new VehicleResponse(v.getId(),v.getRegistrationNumber(),v.getModel(),v.getCapacity(),v.isActive()); }
    }
    public record RouteResponse(Long id, String name, String pickupPoint, String dropPoint, double distanceKm, boolean active) {
        public static RouteResponse from(TransportRoute r) { return new RouteResponse(r.getId(),r.getName(),r.getPickupPoint(),r.getDropPoint(),r.getDistanceKm(),r.isActive()); }
    }
    public record TripResponse(Long id, RouteResponse route, VehicleResponse vehicle, UserResponse driver,
                               LocalDateTime departureTime, TripStatus status, long availableSeats) {
        public static TripResponse from(Trip t, long available) { return new TripResponse(t.getId(),RouteResponse.from(t.getRoute()),VehicleResponse.from(t.getVehicle()),UserResponse.from(t.getDriver()),t.getDepartureTime(),t.getStatus(),available); }
    }
    public record BookingResponse(Long id, Long tripId, UserResponse employee, BookingStatus status,
                                  LocalDateTime bookedAt) {
        public static BookingResponse from(Booking b) { return new BookingResponse(b.getId(),b.getTrip().getId(),UserResponse.from(b.getEmployee()),b.getStatus(),b.getBookedAt()); }
    }
    public record ApiMessage(String message) {}
}
