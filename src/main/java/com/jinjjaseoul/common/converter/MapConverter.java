package com.jinjjaseoul.common.converter;

import com.jinjjaseoul.domain.icon.model.Icon;
import com.jinjjaseoul.domain.map.dto.request.CurationMapRequestDto;
import com.jinjjaseoul.domain.map.dto.request.ThemeMapRequestDto;
import com.jinjjaseoul.domain.map.model.entity.CurationMap;
import com.jinjjaseoul.domain.map.model.entity.ThemeMap;
import com.jinjjaseoul.domain.user.model.User;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class MapConverter {

    public static ThemeMap convertToThemeMapEntity(ThemeMapRequestDto themeMapRequestDto, User user, Icon icon) {
        List<String> keywordList = new ArrayList<>() {{
            String[] keyword = themeMapRequestDto.getKeywordStr().split(",");
            this.addAll(Arrays.asList(keyword));
        }};

        return ThemeMap.builder()
                .user(user)
                .name(themeMapRequestDto.getName())
                .icon(icon)
                .categories(themeMapRequestDto.getCategories())
                .keywordList(keywordList)
                .build();
    }

    public static CurationMap convertToCurationMapEntity(CurationMapRequestDto curationMapRequestDto, User user, Icon icon) {
        return CurationMap.builder()
                .user(user)
                .name(curationMapRequestDto.getName())
                .icon(icon)
                .isMakeTogether(curationMapRequestDto.getIsMakeTogether())
                .isProfileDisplay(curationMapRequestDto.getIsProfileDisplay())
                .isShared(curationMapRequestDto.getIsShared())
                .build();
    }
}