package com.pmpml.transit.neo4j;

import org.springframework.data.neo4j.repository.Neo4jRepository;
import org.springframework.data.neo4j.repository.query.Query;
import java.util.List;
import java.util.Optional;

public interface StopRelationshipRepository extends Neo4jRepository<StopNode, String> {
    Optional<StopNode> findByStopId(String stopId);

    @Query("MATCH (s1:Stop {stopId: })-[:NEXT_STOP*1..10]->(s2:Stop {stopId: }) RETURN s2")
    List<StopNode> findPathBetweenStops(String sourceId, String destId);

    @Query("MATCH (s:Stop)-[:NEXT_STOP]->(next:Stop) WHERE s.stopId =  RETURN next")
    List<StopNode> findNextStops(String stopId);
}
