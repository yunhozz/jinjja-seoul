package com.jinjjaseoul.domain.user.model;

import com.jinjjaseoul.domain.user.dto.query.ProfileQueryDto;
import com.jinjjaseoul.domain.user.dto.query.UserCardQueryDto;

import java.util.List;

public interface UserCustomRepository {

    ProfileQueryDto findProfileById(Long userId);
    List<UserCardQueryDto> findDiligentCurators();
}