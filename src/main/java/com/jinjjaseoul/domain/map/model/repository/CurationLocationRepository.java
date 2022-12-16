package com.jinjjaseoul.domain.map.model.repository;

import com.jinjjaseoul.domain.map.model.entity.CurationLocation;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface CurationLocationRepository extends JpaRepository<CurationLocation, Long> {

    @Query("select cl.id from CurationLocation cl join cl.curationMap cm where cm.id = :id")
    List<Long> findIdsByCurationMapId(@Param("id") Long curationMapId);

    @Modifying(clearAutomatically = true)
    @Query("delete from CurationLocation cl where cl.id in :ids")
    void deleteAllByIds(@Param("ids") List<Long> curationLocationIds);
}