package com.pmpml.transit.entity;

import jakarta.persistence.*;
import lombok.*;
import java.util.ArrayList;
import java.util.List;

@Entity @Table(name = "routes")
@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class Route extends BaseEntity {
    @Column(nullable = false, unique = true, length = 20) private String routeNumber;
    @Column(nullable = false, length = 200) private String name;
    @Column(length = 500) private String description;
    @Column(nullable = false) @Builder.Default private Boolean active = true;
    @OneToMany(mappedBy = "route", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.LAZY)
    @OrderBy("sequenceOrder ASC") @Builder.Default
    private List<RouteStop> routeStops = new ArrayList<>();
    public void addRouteStop(RouteStop rs) { routeStops.add(rs); rs.setRoute(this); }
    public void removeRouteStop(RouteStop rs) { routeStops.remove(rs); rs.setRoute(null); }
}
