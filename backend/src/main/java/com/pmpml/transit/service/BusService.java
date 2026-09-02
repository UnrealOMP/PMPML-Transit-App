package com.pmpml.transit.service;

import com.pmpml.transit.dto.request.CreateBusRequest;
import com.pmpml.transit.dto.response.BusResponse;
import com.pmpml.transit.dto.response.PageResponse;
import com.pmpml.transit.entity.Bus;
import com.pmpml.transit.enums.VehicleType;
import com.pmpml.transit.exception.ResourceNotFoundException;
import com.pmpml.transit.repository.BusRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class BusService {

    private final BusRepository busRepository;

    @Transactional
    public BusResponse createBus(CreateBusRequest request) {
        Bus bus = Bus.builder()
                .busNumber(request.getBusNumber()).name(request.getName())
                .vehicleType(request.getVehicleType()).capacity(request.getCapacity())
                .licensePlate(request.getLicensePlate()).manufacturer(request.getManufacturer())
                .manufacturingYear(request.getManufacturingYear()).build();
        return mapToResponse(busRepository.save(bus));
    }

    public BusResponse getBus(UUID id) {
        return mapToResponse(findBus(id));
    }

    public PageResponse<BusResponse> getAllBuses(int page, int size) {
        Page<Bus> buses = busRepository.findByActiveTrue(PageRequest.of(page, size));
        return PageResponse.<BusResponse>builder()
                .content(buses.getContent().stream().map(this::mapToResponse).toList())
                .page(page).size(size).totalElements(buses.getTotalElements()).totalPages(buses.getTotalPages()).build();
    }

    @Transactional
    public void deleteBus(UUID id) {
        Bus bus = findBus(id);
        bus.setActive(false);
        busRepository.save(bus);
    }

    public Bus findBus(UUID id) {
        return busRepository.findById(id).orElseThrow(() -> new ResourceNotFoundException("Bus not found: " + id));
    }

    public List<Bus> findBusesWithGps() { return busRepository.findByHasGpsTrueAndActiveTrue(); }

    private BusResponse mapToResponse(Bus bus) {
        return BusResponse.builder().id(bus.getId()).busNumber(bus.getBusNumber()).name(bus.getName())
                .vehicleType(bus.getVehicleType()).capacity(bus.getCapacity()).active(bus.getActive())
                .licensePlate(bus.getLicensePlate()).hasGps(bus.getHasGps()).build();
    }
}
