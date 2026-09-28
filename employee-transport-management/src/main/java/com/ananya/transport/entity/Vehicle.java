package com.ananya.transport.entity;
import jakarta.persistence.*;
import lombok.*;

@Entity @Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class Vehicle {
    @Id @GeneratedValue(strategy=GenerationType.IDENTITY) private Long id;
    @Column(nullable=false, unique=true) private String registrationNumber;
    @Column(nullable=false) private String model;
    @Column(nullable=false) private int capacity;
    @Column(nullable=false) @Builder.Default private boolean active = true;
}
