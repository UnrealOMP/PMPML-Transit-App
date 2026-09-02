package com.pmpml.transit.service;

import com.pmpml.transit.dto.request.CreateStopRequest;
import com.pmpml.transit.dto.response.PageResponse;
import com.pmpml.transit.dto.response.StopResponse;
import com.pmpml.transit.entity.Stop;
import com.pmpml.transit.exception.ResourceNotFoundException;
import com.pmpml.transit.repository.StopRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class StopService {

    private final StopRepository stopRepository;

    @Transactional
    public StopResponse createStop(CreateStopRequest request) {
        Stop stop = Stop.builder().stopCode(request.getStopCode()).name(request.getName())
                .latitude(request.getLatitude()).longitude(request.getLongitude())
                .address(request.getAddress()).build();
        return mapToResponse(stopRepository.save(stop));
    }

    public StopResponse getStop(UUID id) { return mapToResponse(findStop(id)); }

    public PageResponse<StopResponse> getAllStops(int page, int size) {
        Page<Stop> stops = stopRepository.findByActiveTrue(PageRequest.of(page, size));
        return PageResponse.<StopResponse>builder()
                .content(stops.getContent().stream().map(this::mapToResponse).toList())
                .page(page).size(size).totalElements(stops.getTotalElements()).totalPages(stops.getTotalPages()).build();
    }

    public Stop findStop(UUID id) { return stopRepository.findById(id).orElseThrow(() -> new ResourceNotFoundException("Stop not found: " + id)); }

    private StopResponse mapToResponse(Stop s) {
        return StopResponse.builder().id(s.getId()).stopCode(s.getStopCode()).name(s.getName())
                .latitude(s.getLatitude()).longitude(s.getLongitude()).address(s.getAddress()).active(s.getActive()).build();
    }
}
