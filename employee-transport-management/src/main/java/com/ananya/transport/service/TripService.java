package com.ananya.transport.service;
import com.ananya.transport.dto.ResponseDtos.*;
import com.ananya.transport.entity.*;
import com.ananya.transport.exception.*;
import com.ananya.transport.repository.*;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.time.LocalDateTime;
import java.util.List;

@Service @RequiredArgsConstructor
public class TripService {
    private final TripRepository trips; private final BookingRepository bookings; private final UserRepository users;
    public List<TripResponse> availableTrips() { return trips.findByDepartureTimeAfterAndStatusOrderByDepartureTime(LocalDateTime.now(),TripStatus.SCHEDULED).stream().map(t->TripResponse.from(t,available(t))).toList(); }
    public List<TripResponse> driverTrips(String email) { User d=user(email); return trips.findByDriverIdOrderByDepartureTimeDesc(d.getId()).stream().map(t->TripResponse.from(t,available(t))).toList(); }
    public TripResponse get(Long id) { Trip t=trips.findById(id).orElseThrow(()->new ResourceNotFoundException("Trip not found")); return TripResponse.from(t,available(t)); }
    @Transactional public TripResponse driverStatus(Long id,TripStatus status,String email) {
        Trip t=trips.findById(id).orElseThrow(()->new ResourceNotFoundException("Trip not found"));
        if(!t.getDriver().getEmail().equalsIgnoreCase(email)) throw new BusinessException("This trip is not assigned to you");
        if(status!=TripStatus.IN_PROGRESS && status!=TripStatus.COMPLETED) throw new BusinessException("Drivers may only start or complete a trip");
        if(status==TripStatus.IN_PROGRESS && t.getStatus()!=TripStatus.SCHEDULED) throw new BusinessException("Only a scheduled trip can be started");
        if(status==TripStatus.COMPLETED && t.getStatus()!=TripStatus.IN_PROGRESS) throw new BusinessException("Only an in-progress trip can be completed");
        t.setStatus(status); return TripResponse.from(t,available(t));
    }
    private long available(Trip t) { return Math.max(0,t.getVehicle().getCapacity()-bookings.countByTripIdAndStatus(t.getId(),BookingStatus.CONFIRMED)); }
    private User user(String email) { return users.findByEmailIgnoreCase(email).orElseThrow(()->new ResourceNotFoundException("User not found")); }
}
