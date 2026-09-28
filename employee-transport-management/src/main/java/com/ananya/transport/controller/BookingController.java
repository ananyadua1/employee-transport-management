package com.ananya.transport.controller;
import com.ananya.transport.dto.ResponseDtos.BookingResponse;
import com.ananya.transport.service.BookingService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController @RequestMapping("/api/bookings") @RequiredArgsConstructor @PreAuthorize("hasRole('EMPLOYEE')")
public class BookingController {
    private final BookingService service;
    @PostMapping("/trip/{tripId}") BookingResponse book(@PathVariable Long tripId,Authentication a) { return service.book(tripId,a.getName()); }
    @PatchMapping("/{id}/cancel") BookingResponse cancel(@PathVariable Long id,Authentication a) { return service.cancel(id,a.getName()); }
    @GetMapping("/me") List<BookingResponse> mine(Authentication a) { return service.mine(a.getName()); }
}
