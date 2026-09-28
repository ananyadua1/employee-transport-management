package com.ananya.transport.service;
import com.ananya.transport.dto.AdminDtos.*;
import com.ananya.transport.dto.ResponseDtos.*;
import com.ananya.transport.entity.*;
import com.ananya.transport.exception.*;
import com.ananya.transport.repository.*;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.util.List;

@Service @RequiredArgsConstructor
public class AdminService {
    private final UserRepository users; private final VehicleRepository vehicles; private final TransportRouteRepository routes;
    private final TripRepository trips; private final BookingRepository bookings; private final PasswordEncoder encoder;
    public UserResponse createUser(CreateUserRequest r) {
        if(users.existsByEmailIgnoreCase(r.email())) throw new BusinessException("Email is already registered");
        return UserResponse.from(users.save(User.builder().name(r.name().trim()).email(r.email().toLowerCase()).password(encoder.encode(r.password())).role(r.role()).build()));
    }
    public List<UserResponse> users() { return users.findAll().stream().map(UserResponse::from).toList(); }
    public VehicleResponse createVehicle(VehicleRequest r) {
        if(vehicles.existsByRegistrationNumberIgnoreCase(r.registrationNumber())) throw new BusinessException("Registration number already exists");
        return VehicleResponse.from(vehicles.save(Vehicle.builder().registrationNumber(r.registrationNumber().toUpperCase()).model(r.model()).capacity(r.capacity()).build()));
    }
    public List<VehicleResponse> vehicles() { return vehicles.findAll().stream().map(VehicleResponse::from).toList(); }
    public RouteResponse createRoute(RouteRequest r) { return RouteResponse.from(routes.save(TransportRoute.builder().name(r.name()).pickupPoint(r.pickupPoint()).dropPoint(r.dropPoint()).distanceKm(r.distanceKm()).build())); }
    public List<RouteResponse> routes() { return routes.findAll().stream().map(RouteResponse::from).toList(); }
    @Transactional public TripResponse scheduleTrip(TripRequest r) {
        TransportRoute route=routes.findById(r.routeId()).orElseThrow(()->new ResourceNotFoundException("Route not found"));
        Vehicle vehicle=vehicles.findById(r.vehicleId()).orElseThrow(()->new ResourceNotFoundException("Vehicle not found"));
        User driver=users.findById(r.driverId()).orElseThrow(()->new ResourceNotFoundException("Driver not found"));
        if(!route.isActive() || !vehicle.isActive()) throw new BusinessException("Route and vehicle must be active");
        if(driver.getRole()!=Role.DRIVER || !driver.isEnabled()) throw new BusinessException("Assigned user must be an active driver");
        if(trips.existsByVehicleIdAndDepartureTimeAndStatusNot(vehicle.getId(),r.departureTime(),TripStatus.CANCELLED)) throw new BusinessException("Vehicle is already scheduled at that time");
        if(trips.existsByDriverIdAndDepartureTimeAndStatusNot(driver.getId(),r.departureTime(),TripStatus.CANCELLED)) throw new BusinessException("Driver is already scheduled at that time");
        Trip t=trips.save(Trip.builder().route(route).vehicle(vehicle).driver(driver).departureTime(r.departureTime()).build());
        return TripResponse.from(t,vehicle.getCapacity());
    }
    @Transactional public TripResponse changeStatus(Long id,TripStatus status) {
        Trip t=trips.findById(id).orElseThrow(()->new ResourceNotFoundException("Trip not found"));
        if(t.getStatus()==TripStatus.COMPLETED || t.getStatus()==TripStatus.CANCELLED) throw new BusinessException("A completed or cancelled trip cannot be changed");
        t.setStatus(status); return TripResponse.from(t,available(t));
    }
    public long available(Trip t) { return Math.max(0,t.getVehicle().getCapacity()-bookings.countByTripIdAndStatus(t.getId(),BookingStatus.CONFIRMED)); }
}
