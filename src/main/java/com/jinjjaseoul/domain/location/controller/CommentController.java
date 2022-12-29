package com.jinjjaseoul.domain.location.controller;

import com.jinjjaseoul.auth.model.UserPrincipal;
import com.jinjjaseoul.common.dto.Response;
import com.jinjjaseoul.domain.location.dto.request.CommentRequestDto;
import com.jinjjaseoul.domain.location.service.CommentService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/comments")
@RequiredArgsConstructor
public class CommentController {

    private final CommentService commentService;

    @PostMapping("/create")
    public Response writeComment(@AuthenticationPrincipal UserPrincipal userPrincipal, @RequestParam Long locationId, @RequestBody CommentRequestDto commentRequestDto) {
        Long commentId = commentService.makeComment(userPrincipal.getId(), locationId, commentRequestDto);
        return Response.success(HttpStatus.CREATED, commentId);
    }

    @PatchMapping("/{id}/delete")
    public Response deleteComment(@PathVariable("id") Long commentId) {
        commentService.deleteComment(commentId);
        return Response.success(HttpStatus.CREATED, "해당 코멘트가 성공적으로 삭제되었습니다.");
    }
}