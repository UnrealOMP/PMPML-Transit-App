package com.pmpml.transit.entity;

import com.pmpml.transit.enums.VehicleType;
import jakarta.persistence.*;
import lombok.*;

@Entity @Table(name = "buses")
@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class Bus extends BaseEntity {
    @Column(nullable = false, unique = true, length = 20) private String busNumber;
    @Column(nullable = false, length = 100) private String name;
    @Enumerated(EnumType.STRING) @Column(nullable = false) private VehicleType vehicleType;
    @Column(nullable = false) private Integer capacity;
    @Column(nullable = false) @Builder.Default private Boolean active = true;
    @Column(length = 50) private String licensePlate;
    @Column(length = 100) private String manufacturer;
    private Integer manufacturingYear;
    @Column(nullable = false) @Builder.Default private Boolean hasGps = true;
    @Column(nullable = false) @Builder.Default private Boolean hasCctv = false;
    @Column(nullable = false) @Builder.Default private Boolean accessible = true;
}
