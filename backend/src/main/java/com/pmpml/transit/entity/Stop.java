package com.pmpml.transit.entity;

import jakarta.persistence.*;
import lombok.*;
import java.math.BigDecimal;

@Entity @Table(name = "stops")
@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class Stop extends BaseEntity {
    @Column(nullable = false, unique = true, length = 20) private String stopCode;
    @Column(nullable = false, length = 200) private String name;
    @Column(nullable = false, precision = 10, scale = 7) private BigDecimal latitude;
    @Column(nullable = false, precision = 10, scale = 7) private BigDecimal longitude;
    @Column(length = 500) private String address;
    @Column(nullable = false) @Builder.Default private Boolean active = true;
}
