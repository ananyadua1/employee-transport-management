package com.ananya.transport.service;

import com.ananya.transport.entity.*;
import com.ananya.transport.exception.BusinessException;
import com.ananya.transport.repository.*;
import org.junit.jupiter.api.*;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.*;
import org.mockito.junit.jupiter.MockitoExtension;
import java.time.LocalDateTime;
import java.util.Optional;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class BookingServiceTest {
    @Mock BookingRepository bookings; @Mock TripRepository trips; @Mock UserRepository users;
    @InjectMocks BookingService service;
    User employee; Trip trip;
    @BeforeEach void setUp() {
        employee=User.builder().id(1L).name("Ananya").email("ananya@example.com").password("x").role(Role.EMPLOYEE).build();
        Vehicle vehicle=Vehicle.builder().id(1L).registrationNumber("KA01AA1000").model("Sedan").capacity(1).build();
        trip=Trip.builder().id(1L).vehicle(vehicle).route(TransportRoute.builder().id(1L).name("Office").pickupPoint("A").dropPoint("B").distanceKm(10).build())
            .driver(User.builder().id(2L).name("Driver").email("driver@example.com").password("x").role(Role.DRIVER).build())
            .departureTime(LocalDateTime.now().plusHours(2)).status(TripStatus.SCHEDULED).build();
    }
    @Test void booksAvailableSeat() {
        when(users.findByEmailIgnoreCase(employee.getEmail())).thenReturn(Optional.of(employee));
        when(trips.findByIdForUpdate(1L)).thenReturn(Optional.of(trip));
        when(bookings.countByTripIdAndStatus(1L,BookingStatus.CONFIRMED)).thenReturn(0L);
        when(bookings.findByTripIdAndEmployeeId(1L,1L)).thenReturn(Optional.empty());
        when(bookings.save(any())).thenAnswer(i->{ Booking b=i.getArgument(0); b.setId(5L); return b; });
        assertEquals(BookingStatus.CONFIRMED,service.book(1L,employee.getEmail()).status());
    }
    @Test void rejectsFullTrip() {
        when(users.findByEmailIgnoreCase(employee.getEmail())).thenReturn(Optional.of(employee));
        when(trips.findByIdForUpdate(1L)).thenReturn(Optional.of(trip));
        when(bookings.countByTripIdAndStatus(1L,BookingStatus.CONFIRMED)).thenReturn(1L);
        assertThrows(BusinessException.class,()->service.book(1L,employee.getEmail()));
    }
}
