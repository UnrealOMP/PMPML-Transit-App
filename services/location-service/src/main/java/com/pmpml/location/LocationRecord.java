package com.pmpml.location;

import jakarta.persistence.*;
import java.math.BigDecimal;
import java.time.Instant;
import java.util.UUID;

@Entity
@Table(name = "bus_locations")
public class LocationRecord {
    @Id private UUID id;
    @Column(nullable = false) private UUID busId;
    @Column(nullable = false, length = 30) private String busNumber;
    @Column(nullable = false, precision = 10, scale = 7) private BigDecimal latitude;
    @Column(nullable = false, precision = 10, scale = 7) private BigDecimal longitude;
    private BigDecimal speed;
    private BigDecimal heading;
    @Column(nullable = false) private Instant recordedAt;
    public UUID getId() { return id; } public void setId(UUID id) { this.id = id; }
    public UUID getBusId() { return busId; } public void setBusId(UUID busId) { this.busId = busId; }
    public String getBusNumber() { return busNumber; } public void setBusNumber(String busNumber) { this.busNumber = busNumber; }
    public BigDecimal getLatitude() { return latitude; } public void setLatitude(BigDecimal latitude) { this.latitude = latitude; }
    public BigDecimal getLongitude() { return longitude; } public void setLongitude(BigDecimal longitude) { this.longitude = longitude; }
    public BigDecimal getSpeed() { return speed; } public void setSpeed(BigDecimal speed) { this.speed = speed; }
    public BigDecimal getHeading() { return heading; } public void setHeading(BigDecimal heading) { this.heading = heading; }
    public Instant getRecordedAt() { return recordedAt; } public void setRecordedAt(Instant recordedAt) { this.recordedAt = recordedAt; }
}
