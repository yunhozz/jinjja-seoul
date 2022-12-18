package com.jinjjaseoul.domain.location.model.repository.location;

import com.jinjjaseoul.domain.location.dto.query.LocationQueryDto;

public interface LocationCustomRepository {

    LocationQueryDto findLocationInfoById(Long locationId);
}