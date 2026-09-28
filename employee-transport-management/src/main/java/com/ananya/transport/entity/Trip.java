package com.ananya.transport.entity;
import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDateTime;

@Entity @Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class Trip {
    @Id @GeneratedValue(strategy=GenerationType.IDENTITY) private Long id;
    @ManyToOne(optional=false, fetch=FetchType.EAGER) private TransportRoute route;
    @ManyToOne(optional=false, fetch=FetchType.EAGER) private Vehicle vehicle;
    @ManyToOne(optional=false, fetch=FetchType.EAGER) private User driver;
    @Column(nullable=false) private LocalDateTime departureTime;
    @Enumerated(EnumType.STRING) @Column(nullable=false) @Builder.Default private TripStatus status = TripStatus.SCHEDULED;
    @Version private Long version;
}
