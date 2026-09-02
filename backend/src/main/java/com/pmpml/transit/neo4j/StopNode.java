package com.pmpml.transit.neo4j;

import org.springframework.data.neo4j.core.schema.Id;
import org.springframework.data.neo4j.core.schema.Node;
import org.springframework.data.neo4j.core.schema.Property;
import lombok.*;

@Node("Stop")
@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class StopNode {
    @Id private String stopId;
    @Property("name") private String name;
    @Property("latitude") private double latitude;
    @Property("longitude") private double longitude;
}
