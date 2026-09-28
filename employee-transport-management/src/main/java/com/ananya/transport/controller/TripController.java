package com.ananya.transport.controller;
import com.ananya.transport.dto.AdminDtos.TripStatusRequest;
import com.ananya.transport.dto.ResponseDtos.TripResponse;
import com.ananya.transport.service.TripService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController @RequestMapping("/api/trips") @RequiredArgsConstructor
public class TripController {
    private final TripService service;
    @GetMapping @PreAuthorize("hasAnyRole('EMPLOYEE','ADMIN')") List<TripResponse> available() { return service.availableTrips(); }
    @GetMapping("/{id}") TripResponse get(@PathVariable Long id) { return service.get(id); }
    @GetMapping("/driver/me") @PreAuthorize("hasRole('DRIVER')") List<TripResponse> driverTrips(Authentication a) { return service.driverTrips(a.getName()); }
    @PatchMapping("/{id}/driver-status") @PreAuthorize("hasRole('DRIVER')") TripResponse update(@PathVariable Long id,@Valid @RequestBody TripStatusRequest r,Authentication a) { return service.driverStatus(id,r.status(),a.getName()); }
}
