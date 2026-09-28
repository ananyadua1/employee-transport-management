package com.ananya.transport.repository;
import com.ananya.transport.entity.*;
import jakarta.persistence.LockModeType;
import org.springframework.data.jpa.repository.*;
import org.springframework.data.repository.query.Param;
import java.time.LocalDateTime;
import java.util.*;
public interface TripRepository extends JpaRepository<Trip,Long> {
    @Lock(LockModeType.PESSIMISTIC_WRITE) @Query("select t from Trip t where t.id=:id") Optional<Trip> findByIdForUpdate(@Param("id") Long id);
    List<Trip> findByDepartureTimeAfterAndStatusOrderByDepartureTime(LocalDateTime now, TripStatus status);
    List<Trip> findByDriverIdOrderByDepartureTimeDesc(Long driverId);
    boolean existsByVehicleIdAndDepartureTimeAndStatusNot(Long vehicleId, LocalDateTime time, TripStatus status);
    boolean existsByDriverIdAndDepartureTimeAndStatusNot(Long driverId, LocalDateTime time, TripStatus status);
}
