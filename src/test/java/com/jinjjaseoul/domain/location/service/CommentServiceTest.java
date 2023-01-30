package com.jinjjaseoul.domain.location.service;

import com.jinjjaseoul.common.enums.Role;
import com.jinjjaseoul.domain.icon.model.Icon;
import com.jinjjaseoul.domain.icon.model.IconRepository;
import com.jinjjaseoul.domain.location.dto.request.CommentRequestDto;
import com.jinjjaseoul.domain.location.model.entity.Address;
import com.jinjjaseoul.domain.location.model.entity.Comment;
import com.jinjjaseoul.domain.location.model.entity.Location;
import com.jinjjaseoul.domain.location.model.repository.CommentRepository;
import com.jinjjaseoul.domain.location.model.repository.location.LocationRepository;
import com.jinjjaseoul.domain.location.service.exception.CommentAlreadyWrittenException;
import com.jinjjaseoul.domain.user.model.User;
import com.jinjjaseoul.domain.user.model.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyBoolean;
import static org.mockito.BDDMockito.anyLong;
import static org.mockito.BDDMockito.given;

@ExtendWith(MockitoExtension.class)
class CommentServiceTest {

    @InjectMocks
    CommentService commentService;

    @Mock
    UserRepository userRepository;

    @Mock
    LocationRepository locationRepository;

    @Mock
    IconRepository iconRepository;

    @Mock
    CommentRepository commentRepository;

    User user;
    Icon icon;
    Location location;

    @BeforeEach
    void beforeEach() {
        user = createUser("test@gmail.com", "tester");
        icon = new Icon("test.ico", "test");
        location = createLocation("test-location");
    }

    @Test
    @DisplayName("댓글 작성")
    void makeComment() throws Exception {
        // given
        Long userId = 1L;
        Long locationId = 100L;
        Long iconId = 5L;
        CommentRequestDto commentRequestDto = new CommentRequestDto(iconId, "This is test");

        Comment comment = Comment.builder()
                .user(user)
                .icon(icon)
                .location(location)
                .content(commentRequestDto.getContent())
                .build();

        given(userRepository.getReferenceById(anyLong())).willReturn(user);
        given(locationRepository.getReferenceById(anyLong())).willReturn(location);
        given(commentRepository.existsByUserAndLocationAndIsDeleted(any(User.class), any(Location.class), anyBoolean())).willReturn(false);
        given(iconRepository.getReferenceById(anyLong())).willReturn(icon);
        given(commentRepository.save(any(Comment.class))).willReturn(comment);

        // when
        Long result = commentService.makeComment(userId, locationId, commentRequestDto);

        // then
        assertDoesNotThrow(() -> result);
        assertThat(result).isEqualTo(comment.getId());
        assertThat(user.getNumOfComment()).isEqualTo(1);
    }

    @Test
    @DisplayName("댓글 작성 시 중복 검증 실패")
    void makeCommentThrowDuplicateException() throws Exception {
        // given
        Long userId = 1L;
        Long locationId = 100L;
        Long iconId = 5L;
        CommentRequestDto commentRequestDto = new CommentRequestDto(iconId, "This is test");

        given(userRepository.getReferenceById(anyLong())).willReturn(user);
        given(locationRepository.getReferenceById(anyLong())).willReturn(location);
        given(commentRepository.existsByUserAndLocationAndIsDeleted(any(User.class), any(Location.class), anyBoolean())).willReturn(true);

        // then
        assertThrows(CommentAlreadyWrittenException.class, () -> commentService.makeComment(userId, locationId, commentRequestDto));
    }

    @Test
    @DisplayName("댓글 삭제")
    void deleteComment() throws Exception {
        // given
        Long commentId = 100L;
        Comment comment = Comment.builder()
                .user(user)
                .icon(icon)
                .location(location)
                .content("This is test")
                .build();

        given(commentRepository.findWithUserById(anyLong())).willReturn(Optional.of(comment));

        // when
        user.addNumOfComment(); // to test subtracting number of comment

        // then
        assertDoesNotThrow(() -> commentService.deleteComment(commentId));
        assertThat(comment.isDeleted()).isTrue();
        assertThat(user.getNumOfComment()).isEqualTo(0);
    }

    private User createUser(String email, String name) {
        return User.builder()
                .email(email)
                .name(name)
                .role(Role.USER)
                .build();
    }

    private Location createLocation(String name) {
        return Location.builder()
                .name(name)
                .address(new Address("si", "gu", "dong", "etc"))
                .nx("100")
                .ny("200")
                .build();
    }
}