package com.jinjjaseoul.domain.map.model.repository.curation_map;

import com.jinjjaseoul.domain.map.model.entity.CurationMap;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CurationMapRepository extends JpaRepository<CurationMap, Long>, CurationMapCustomRepository {
}