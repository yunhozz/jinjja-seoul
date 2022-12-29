package com.jinjjaseoul.domain.location.service;

import com.jinjjaseoul.domain.icon.model.Icon;
import com.jinjjaseoul.domain.icon.model.IconRepository;
import com.jinjjaseoul.domain.location.dto.request.CommentRequestDto;
import com.jinjjaseoul.domain.location.model.entity.Comment;
import com.jinjjaseoul.domain.location.model.entity.Location;
import com.jinjjaseoul.domain.location.model.repository.CommentRepository;
import com.jinjjaseoul.domain.location.model.repository.location.LocationRepository;
import com.jinjjaseoul.domain.location.service.exception.CommentAlreadyWrittenException;
import com.jinjjaseoul.domain.location.service.exception.CommentNotFoundException;
import com.jinjjaseoul.domain.user.model.User;
import com.jinjjaseoul.domain.user.model.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class CommentService {

    private final UserRepository userRepository;
    private final LocationRepository locationRepository;
    private final IconRepository iconRepository;
    private final CommentRepository commentRepository;

    @Transactional
    public Long makeComment(Long userId, Long locationId, CommentRequestDto commentRequestDto) {
        User user = userRepository.getReferenceById(userId);
        Location location = locationRepository.getReferenceById(locationId);
        validateAlreadyWrittenOrNot(user, location); // 해당 장소에 대한 유저의 코멘트가 이미 존재하는지 검증

        Icon icon = iconRepository.getReferenceById(commentRequestDto.getIconId());
        Comment comment = createComment(commentRequestDto.getContent(), user, location, icon);
        comment.addUserNumOfComment(); // 유저의 댓글 수 +1

        return commentRepository.save(comment).getId();
    }

    @Transactional
    public void deleteComment(Long commentId) {
        Comment comment = commentRepository.findWithUserById(commentId)
                .orElseThrow(CommentNotFoundException::new);
        comment.subtractUserNumOfComment(); // 유저의 댓글 수 -1
        comment.delete();
    }

    private Comment createComment(String content, User user, Location location, Icon icon) {
        return Comment.builder()
                .user(user)
                .location(location)
                .icon(icon)
                .content(content)
                .build();
    }

    private void validateAlreadyWrittenOrNot(User user, Location location) {
        if (commentRepository.existsByUserAndLocationAndIsDeleted(user, location, false)) {
            throw new CommentAlreadyWrittenException();
        }
    }
}