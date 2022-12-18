package com.jinjjaseoul.domain.location.service;

import com.jinjjaseoul.domain.location.dto.request.LocationRequestDto;
import com.jinjjaseoul.domain.location.model.entity.Address;
import com.jinjjaseoul.domain.location.model.entity.Location;
import com.jinjjaseoul.domain.location.model.repository.location.LocationRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class LocationService {

    private final LocationRepository locationRepository;

    @Transactional
    public Long createLocation(LocationRequestDto locationRequestDto) {
        Address address = new Address(locationRequestDto.getSi(), locationRequestDto.getGu(), locationRequestDto.getDong(), locationRequestDto.getEtc());
        Location location = Location.builder()
                .name(locationRequestDto.getName())
                .address(address)
                .nx(locationRequestDto.getNx())
                .ny(locationRequestDto.getNy())
                .build();

        return locationRepository.save(location).getId();
    }

    @Transactional
    public void deleteLocation(Long locationId) {
        Location location = locationRepository.getReferenceById(locationId);
        locationRepository.delete(location);
    }
}