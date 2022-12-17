package com.jinjjaseoul.domain.bookmark.controller;

import com.jinjjaseoul.auth.model.UserPrincipal;
import com.jinjjaseoul.common.dto.Response;
import com.jinjjaseoul.domain.bookmark.service.BookmarkService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/bookmarks")
@RequiredArgsConstructor
public class BookmarkController {

    private final BookmarkService bookmarkService;

    @PostMapping("/curations/{id}")
    public Response makeLikesOfCurationMap(@AuthenticationPrincipal UserPrincipal userPrincipal, @PathVariable("id") Long curationMapId) {
        Long curationLikesId = bookmarkService.makeLikesOfCurationMap(userPrincipal, curationMapId);
        return Response.success(HttpStatus.CREATED, curationLikesId);
    }

    @PostMapping("/location/{id}")
    public Response makeBookmarkOfLocation(@AuthenticationPrincipal UserPrincipal userPrincipal, @PathVariable("id") Long locationId) {
        Long locationBookmarkId = bookmarkService.makeBookmarkOfLocation(userPrincipal, locationId);
        return Response.success(HttpStatus.CREATED, locationBookmarkId);
    }

    @DeleteMapping("/curations/{id}")
    public Response cancelLikesOfCurationMap(@PathVariable("id") Long curationLikesId) {
        bookmarkService.deleteCurationLikes(curationLikesId);
        return Response.success(HttpStatus.NO_CONTENT, "큐레이션 지도에 대한 좋아요가 취소되었습니다.");
    }

    @DeleteMapping("/location/{id}")
    public Response cancelBookmarkOfLocation(@PathVariable("id") Long locationBookmarkId) {
        bookmarkService.deleteLocationBookmark(locationBookmarkId);
        return Response.success(HttpStatus.NO_CONTENT, "북마크가 취소되었습니다.");
    }
}