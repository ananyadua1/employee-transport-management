package com.ananya.transport.entity;
import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDateTime;

@Entity @Table(uniqueConstraints=@UniqueConstraint(columnNames={"employee_id","trip_id"}))
@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class Booking {
    @Id @GeneratedValue(strategy=GenerationType.IDENTITY) private Long id;
    @ManyToOne(optional=false) @JoinColumn(name="employee_id") private User employee;
    @ManyToOne(optional=false) @JoinColumn(name="trip_id") private Trip trip;
    @Enumerated(EnumType.STRING) @Column(nullable=false) @Builder.Default private BookingStatus status = BookingStatus.CONFIRMED;
    @Column(nullable=false) @Builder.Default private LocalDateTime bookedAt = LocalDateTime.now();
}
