package com.ananya.transport.repository;
import com.ananya.transport.entity.*;
import org.springframework.data.jpa.repository.*;
import java.util.*;
public interface BookingRepository extends JpaRepository<Booking,Long> {
    long countByTripIdAndStatus(Long tripId, BookingStatus status);
    Optional<Booking> findByTripIdAndEmployeeId(Long tripId, Long employeeId);
    List<Booking> findByEmployeeIdOrderByBookedAtDesc(Long employeeId);
    List<Booking> findByTripIdOrderByBookedAt(Long tripId);
}
