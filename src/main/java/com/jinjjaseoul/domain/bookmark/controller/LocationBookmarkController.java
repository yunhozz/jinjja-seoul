package com.jinjjaseoul.domain.bookmark.controller;

import com.jinjjaseoul.auth.model.UserPrincipal;
import com.jinjjaseoul.common.dto.Response;
import com.jinjjaseoul.domain.bookmark.dto.LocationCardResponseDto;
import com.jinjjaseoul.domain.bookmark.service.BookmarkService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/location/bookmarks")
@RequiredArgsConstructor
public class LocationBookmarkController {

    private final BookmarkService bookmarkService;

    @GetMapping
    public Response getLocationBookmarkListByUser(@AuthenticationPrincipal UserPrincipal userPrincipal) {
        List<LocationCardResponseDto> locationCardList = bookmarkService.findLocationCardListByUserId(userPrincipal.getId());
        return Response.success(HttpStatus.OK, locationCardList);
    }

    @PostMapping
    public Response makeBookmarkOfLocation(@AuthenticationPrincipal UserPrincipal userPrincipal, @RequestParam Long locationId) {
        Long locationBookmarkId = bookmarkService.makeBookmarkOfLocation(userPrincipal, locationId);
        return Response.success(HttpStatus.CREATED, locationBookmarkId);
    }

    @DeleteMapping("/{id}/cancel")
    public Response cancelBookmarkOfLocation(@PathVariable("id") Long locationBookmarkId) {
        bookmarkService.deleteLocationBookmark(locationBookmarkId);
        return Response.success(HttpStatus.NO_CONTENT, "북마크가 취소되었습니다.");
    }
}