package com.ananya.transport.entity;
import jakarta.persistence.*;
import lombok.*;

@Entity @Table(name="transport_routes") @Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class TransportRoute {
    @Id @GeneratedValue(strategy=GenerationType.IDENTITY) private Long id;
    @Column(nullable=false) private String name;
    @Column(nullable=false) private String pickupPoint;
    @Column(nullable=false) private String dropPoint;
    @Column(nullable=false) private double distanceKm;
    @Column(nullable=false) @Builder.Default private boolean active = true;
}
