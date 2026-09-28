package com.ananya.transport.repository;
import com.ananya.transport.entity.Vehicle;
import org.springframework.data.jpa.repository.JpaRepository;
public interface VehicleRepository extends JpaRepository<Vehicle,Long> { boolean existsByRegistrationNumberIgnoreCase(String number); }
