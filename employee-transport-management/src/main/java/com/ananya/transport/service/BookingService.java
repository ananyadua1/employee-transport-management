package com.ananya.transport.service;
import com.ananya.transport.dto.ResponseDtos.BookingResponse;
import com.ananya.transport.entity.*;
import com.ananya.transport.exception.*;
import com.ananya.transport.repository.*;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.time.LocalDateTime;
import java.util.List;

@Service @RequiredArgsConstructor
public class BookingService {
    private final BookingRepository bookings; private final TripRepository trips; private final UserRepository users;
    @Transactional public BookingResponse book(Long tripId,String email) {
        User employee=user(email); if(employee.getRole()!=Role.EMPLOYEE) throw new BusinessException("Only employees can book trips");
        Trip trip=trips.findByIdForUpdate(tripId).orElseThrow(()->new ResourceNotFoundException("Trip not found"));
        if(trip.getStatus()!=TripStatus.SCHEDULED || !trip.getDepartureTime().isAfter(LocalDateTime.now())) throw new BusinessException("Trip is no longer bookable");
        long occupied=bookings.countByTripIdAndStatus(tripId,BookingStatus.CONFIRMED);
        if(occupied>=trip.getVehicle().getCapacity()) throw new BusinessException("No seats available");
        var existing=bookings.findByTripIdAndEmployeeId(tripId,employee.getId());
        if(existing.isPresent()) {
            Booking b=existing.get();
            if(b.getStatus()==BookingStatus.CONFIRMED) throw new BusinessException("You already booked this trip");
            b.setStatus(BookingStatus.CONFIRMED); b.setBookedAt(LocalDateTime.now()); return BookingResponse.from(b);
        }
        return BookingResponse.from(bookings.save(Booking.builder().employee(employee).trip(trip).build()));
    }
    @Transactional public BookingResponse cancel(Long id,String email) {
        Booking b=bookings.findById(id).orElseThrow(()->new ResourceNotFoundException("Booking not found"));
        if(!b.getEmployee().getEmail().equalsIgnoreCase(email)) throw new BusinessException("You can cancel only your own booking");
        if(b.getStatus()==BookingStatus.CANCELLED) throw new BusinessException("Booking is already cancelled");
        if(!b.getTrip().getDepartureTime().isAfter(LocalDateTime.now()) || b.getTrip().getStatus()!=TripStatus.SCHEDULED) throw new BusinessException("This booking can no longer be cancelled");
        b.setStatus(BookingStatus.CANCELLED); return BookingResponse.from(b);
    }
    public List<BookingResponse> mine(String email) { return bookings.findByEmployeeIdOrderByBookedAtDesc(user(email).getId()).stream().map(BookingResponse::from).toList(); }
    public List<BookingResponse> forTrip(Long tripId) { if(!trips.existsById(tripId)) throw new ResourceNotFoundException("Trip not found"); return bookings.findByTripIdOrderByBookedAt(tripId).stream().map(BookingResponse::from).toList(); }
    private User user(String email) { return users.findByEmailIgnoreCase(email).orElseThrow(()->new ResourceNotFoundException("User not found")); }
}
