package com.backend.domain.comment.service;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.Mockito.*;

import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import net.jqwik.api.Arbitraries;
import net.jqwik.api.Arbitrary;

import com.backend.domain.comment.dto.request.CommentRequest;
import com.backend.domain.comment.dto.response.CommentResponse;
import com.backend.domain.comment.entity.Comment;
import com.backend.domain.comment.exception.CommentErrorCode;
import com.backend.domain.comment.exception.CommentExpection;
import com.backend.domain.comment.repository.CommentRepository;
import com.backend.domain.fishingtrippost.entity.FishingTripPost;
import com.backend.domain.fishingtrippost.exception.FishingTripPostErrorCode;
import com.backend.domain.fishingtrippost.exception.FishingTripPostException;
import com.backend.domain.fishingtrippost.repository.FishingTripPostRepository;
import com.backend.global.dto.request.GlobalRequest;
import com.backend.global.dto.response.ScrollResponse;
import com.backend.global.util.BaseTest;

import com.navercorp.fixturemonkey.ArbitraryBuilder;

@ExtendWith(MockitoExtension.class)
class CommentServiceTest extends BaseTest {

	@Mock
	private CommentRepository commentRepository;

	@Mock
	private FishingTripPostRepository fishingTripPostRepository;

	@InjectMocks
	private CommentServiceImpl commentService;

	private final Arbitrary<String> englishStringLength = Arbitraries.strings()
		.withCharRange('a', 'z')
		.withCharRange('A', 'Z')
		.ofMinLength(1).ofMaxLength(10);

	private final ArbitraryBuilder<CommentRequest.Create> createArbitraryBuilder = fixtureMonkeyRecord
		.giveMeBuilder(CommentRequest.Create.class)
		.set("content", englishStringLength)
		.set("parentId", null);

	@Test
	@DisplayName("댓글 저장 [Parent Null] [Service] - Success")
	void t01() {
		// Given
		Long givenMemberId = 1L;
		Long givenFishingTripPostId = 1L;

		CommentRequest.Create givenRequestDto = createArbitraryBuilder.sample();

		Comment givenComment = fixtureMonkeyBuilder.giveMeBuilder(Comment.class)
			.set("commentId", 1L)
			.set("memberId", givenMemberId)
			.set("fishingTripPostId", givenFishingTripPostId)
			.set("content", givenRequestDto.content())
			.set("parentId", null)
			.set("childCount", 0)
			.sample();

		when(fishingTripPostRepository.existsById(givenFishingTripPostId)).thenReturn(true);
		when(fishingTripPostRepository.findById(givenFishingTripPostId))
			.thenReturn(Optional.of(mock(FishingTripPost.class)));

		when(commentRepository.save(any(Comment.class))).thenReturn(givenComment);

		// When
		Long savedCommentId = commentService.createComment(givenFishingTripPostId, givenMemberId, givenRequestDto);

		// Then
		assertThat(savedCommentId).isEqualTo(givenComment.getCommentId());
	}

	@Test
	@DisplayName("댓글 저장 [Service] - Success")
	void t02() {
		// Given
		Long givenMemberId = 1L;
		Long givenFishingTripPostId = 1L;

		CommentRequest.Create givenRequestDto = createArbitraryBuilder
			.set("parentId", 1L).sample();

		Comment givenComment = fixtureMonkeyBuilder.giveMeBuilder(Comment.class)
			.set("commentId", 1L)
			.set("memberId", givenMemberId)
			.set("fishingTripPostId", givenFishingTripPostId)
			.set("content", givenRequestDto.content())
			.set("parentId", 1L)
			.set("childCount", 0)
			.sample();

		when(commentRepository.existsByCommentId((givenRequestDto.parentId()))).thenReturn(true);
		when(fishingTripPostRepository.findById(givenFishingTripPostId))
			.thenReturn(Optional.of(mock(FishingTripPost.class)));
		when(commentRepository.save(any(Comment.class))).thenReturn(givenComment);

		when(fishingTripPostRepository.existsById(givenFishingTripPostId)).thenReturn(true);
		doNothing().when(commentRepository).addChildCount(givenRequestDto.parentId());

		// When
		Long savedCommentId = commentService.createComment(givenFishingTripPostId, givenMemberId, givenRequestDto);

		// Then
		assertThat(savedCommentId).isEqualTo(givenComment.getCommentId());
		verify(commentRepository, times(1)).addChildCount(givenRequestDto.parentId());
	}

	@Test
	@DisplayName("댓글 저장 [Fishing Trip Post Not Found] [Service] - Fail")
	void t03() {
		// Given
		Long givenMemberId = 1L;
		Long givenFishingTripPostId = 1L;

		CommentRequest.Create givenRequestDto = createArbitraryBuilder.sample();

		when(fishingTripPostRepository.existsById(givenFishingTripPostId)).thenReturn(false);

		// When & Then
		assertThatThrownBy(() -> commentService.createComment(givenFishingTripPostId, givenMemberId, givenRequestDto))
			.isExactlyInstanceOf(FishingTripPostException.class)
			.hasMessage(FishingTripPostErrorCode.FISHING_TRIP_POST_NOT_FOUND.getMessage());
	}

	@Test
	@DisplayName("댓글 저장 [Fishing Trip Post Not Found] [Service] - Fail")
	void t04() {
		// Given
		Long givenMemberId = 1L;
		Long givenFishingTripPostId = 1L;

		CommentRequest.Create givenRequestDto = createArbitraryBuilder
			.set("parentId", 1L)
			.sample();

		when(fishingTripPostRepository.existsById(givenFishingTripPostId)).thenReturn(true);
		when(commentRepository.existsByCommentId(givenRequestDto.parentId())).thenReturn(false);

		// When & Then
		assertThatThrownBy(() -> commentService.createComment(givenFishingTripPostId, givenMemberId, givenRequestDto))
			.isExactlyInstanceOf(CommentExpection.class)
			.hasMessage(CommentErrorCode.PARENT_NOT_FOUND.getMessage());
	}

	@Test
	@DisplayName("댓글 조회 [Service] - Fail")
	void t05() {
		// Given
		Long givenFishingTripPostId = 1L;
		Long givenMemberId = 1L;

		GlobalRequest.CursorRequest givenCursorRequestDto = new GlobalRequest.CursorRequest(
			null,
			null,
			null,
			null,
			null,
			10
		);

		CommentRequest.Search givenRequestDto = new CommentRequest.Search(null);

		List<CommentResponse.Detail> givenDetailList = fixtureMonkeyRecord
			.giveMeBuilder(CommentResponse.Detail.class)
			.set("parentId", null)
			.sampleList(10);

		ScrollResponse<CommentResponse.Detail> givenScrollResponse = fixtureMonkeyRecord
			.giveMeBuilder(ScrollResponse.class)
			.set("content", givenDetailList)
			.sample();

		when(
			commentRepository.findDetailByFishTripPostId(
				givenFishingTripPostId,
				givenMemberId,
				givenCursorRequestDto,
				givenRequestDto)
		).thenReturn(givenScrollResponse);

		// When
		ScrollResponse<CommentResponse.Detail> getDetailList = commentService.getDetailList(
			givenFishingTripPostId,
			givenMemberId,
			givenCursorRequestDto,
			givenRequestDto
		);

		// Then
		assertThat(getDetailList.content()).isEqualTo(givenDetailList);
	}

	@Test
	@DisplayName("댓글 수정 [Service] - Success")
	void t06() {
		// Given
		Long givenMemberId = 1L;
		Long givenFishingTripPostId = 1L;
		Long givenCommentId = 1L;

		CommentRequest.Update givenRequestDto = fixtureMonkeyValidation.giveMeOne(CommentRequest.Update.class);

		Comment givenComment = fixtureMonkeyBuilder.giveMeBuilder(Comment.class)
			.set("commentId", givenCommentId)
			.set("fishingTripPostId", givenFishingTripPostId)
			.set("memberId", givenMemberId)
			.sample();

		when(commentRepository.findByCommentId(givenCommentId)).thenReturn(Optional.ofNullable(givenComment));

		// When
		commentService.updateComment(givenMemberId, givenCommentId, givenFishingTripPostId, givenRequestDto);

		// Then
		verify(commentRepository, times(1)).findByCommentId(givenCommentId);
	}

	@Test
	@DisplayName("댓글 수정 [Comment Not Found] [Service] - Fail")
	void t07() {
		// Given
		Long givenMemberId = 1L;
		Long givenFishingTripPostId = 1L;
		Long givenCommentId = 1L;

		CommentRequest.Update givenRequestDto = fixtureMonkeyValidation.giveMeOne(CommentRequest.Update.class);

		when(commentRepository.findByCommentId(givenCommentId)).thenReturn(Optional.empty());

		// When & Then
		assertThatThrownBy(
			() -> commentService.updateComment(givenMemberId, givenCommentId, givenFishingTripPostId, givenRequestDto))
			.isExactlyInstanceOf(CommentExpection.class)
			.hasMessage(CommentErrorCode.COMMENT_NOT_FOUND.getMessage());
	}

	@Test
	@DisplayName("댓글 수정 [Comment Unauthorized Author] [Service] - Fail")
	void t08() {
		// Given
		Long givenMemberId = 1L;
		Long givenFishingTripPostId = 1L;
		Long givenCommentId = 1L;

		Comment givenComment = fixtureMonkeyBuilder.giveMeBuilder(Comment.class)
			.set("commentId", givenCommentId)
			.set("fishingTripPostId", givenFishingTripPostId)
			.set("memberId", 2L)
			.sample();

		CommentRequest.Update givenRequestDto = fixtureMonkeyValidation.giveMeOne(CommentRequest.Update.class);

		when(commentRepository.findByCommentId(givenCommentId)).thenReturn(Optional.ofNullable(givenComment));

		// When & Then
		assertThatThrownBy(
			() -> commentService.updateComment(givenMemberId, givenCommentId, givenFishingTripPostId, givenRequestDto))
			.isExactlyInstanceOf(CommentExpection.class)
			.hasMessage(CommentErrorCode.COMMENT_UNAUTHORIZED_AUTHOR.getMessage());
	}

	@Test
	@DisplayName("댓글 삭제 [Service] - Success")
	void t09() {
		// Given
		Long givenMemberId = 1L;
		Long givenFishingTripPostId = 1L;
		Long givenCommentId = 1L;

		Comment givenComment = fixtureMonkeyBuilder.giveMeBuilder(Comment.class)
			.set("commentId", givenCommentId)
			.set("fishingTripPostId", givenFishingTripPostId)
			.set("memberId", givenMemberId)
			.sample();

		when(commentRepository.findByCommentId(givenCommentId)).thenReturn(Optional.of(givenComment));
		when(commentRepository.deleteByParentId(givenCommentId)).thenReturn(1L);
		doNothing().when(commentRepository).minusChildCount(givenComment.getParentId());

		// ✅ 추가: 게시글 존재하는 경우 mocking
		when(fishingTripPostRepository.findById(givenFishingTripPostId))
			.thenReturn(Optional.of(mock(FishingTripPost.class)));

		// When
		commentService.deleteComment(givenMemberId, givenCommentId, givenFishingTripPostId);

		// Then
		verify(commentRepository, times(1)).deleteByParentId(givenCommentId);
		verify(commentRepository, times(1)).minusChildCount(givenComment.getParentId());
	}


	@Test
	@DisplayName("댓글 삭제 [Comment Not Found] [Service] - Fail")
	void t10() {
		// Given
		Long givenMemberId = 1L;
		Long givenFishingTripPostId = 1L;
		Long givenCommentId = 1L;

		when(commentRepository.findByCommentId(givenCommentId)).thenReturn(Optional.empty());

		// When & Then
		assertThatThrownBy(
			() -> commentService.deleteComment(givenMemberId, givenCommentId, givenFishingTripPostId))
			.isExactlyInstanceOf(CommentExpection.class)
			.hasMessage(CommentErrorCode.COMMENT_NOT_FOUND.getMessage());
	}

	@Test
	@DisplayName("댓글 삭제 [Comment Unauthorized Author] [Service] - Success")
	void t11() {
		// Given
		Long givenMemberId = 1L;
		Long givenFishingTripPostId = 1L;
		Long givenCommentId = 1L;

		Comment givenComment = fixtureMonkeyBuilder.giveMeBuilder(Comment.class)
			.set("commentId", givenCommentId)
			.set("fishingTripPostId", givenFishingTripPostId)
			.set("memberId", 2L)
			.sample();

		when(commentRepository.findByCommentId(givenCommentId)).thenReturn(Optional.ofNullable(givenComment));

		// When & Then
		assertThatThrownBy(
			() -> commentService.deleteComment(givenMemberId, givenCommentId, givenFishingTripPostId))
			.isExactlyInstanceOf(CommentExpection.class)
			.hasMessage(CommentErrorCode.COMMENT_UNAUTHORIZED_AUTHOR.getMessage());
	}
}