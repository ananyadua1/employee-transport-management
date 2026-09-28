package com.ananya.transport.controller;
import com.ananya.transport.dto.AdminDtos.*;
import com.ananya.transport.dto.ResponseDtos.*;
import com.ananya.transport.service.*;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.*;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController @RequestMapping("/api/admin") @RequiredArgsConstructor @PreAuthorize("hasRole('ADMIN')")
public class AdminController {
    private final AdminService admin; private final BookingService bookings;
    @PostMapping("/users") ResponseEntity<UserResponse> createUser(@Valid @RequestBody CreateUserRequest r) { return ResponseEntity.status(201).body(admin.createUser(r)); }
    @GetMapping("/users") List<UserResponse> users() { return admin.users(); }
    @PostMapping("/vehicles") ResponseEntity<VehicleResponse> createVehicle(@Valid @RequestBody VehicleRequest r) { return ResponseEntity.status(201).body(admin.createVehicle(r)); }
    @GetMapping("/vehicles") List<VehicleResponse> vehicles() { return admin.vehicles(); }
    @PostMapping("/routes") ResponseEntity<RouteResponse> createRoute(@Valid @RequestBody RouteRequest r) { return ResponseEntity.status(201).body(admin.createRoute(r)); }
    @GetMapping("/routes") List<RouteResponse> routes() { return admin.routes(); }
    @PostMapping("/trips") ResponseEntity<TripResponse> schedule(@Valid @RequestBody TripRequest r) { return ResponseEntity.status(201).body(admin.scheduleTrip(r)); }
    @PatchMapping("/trips/{id}/status") TripResponse status(@PathVariable Long id,@Valid @RequestBody TripStatusRequest r) { return admin.changeStatus(id,r.status()); }
    @GetMapping("/trips/{id}/bookings") List<BookingResponse> tripBookings(@PathVariable Long id) { return bookings.forTrip(id); }
}
