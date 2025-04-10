package com.backend.domain.comment.repository;

import static org.assertj.core.api.Assertions.*;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.FilterType;
import org.springframework.context.annotation.Import;
import org.springframework.stereotype.Repository;

import net.jqwik.api.Arbitraries;
import net.jqwik.api.Arbitrary;

import com.backend.domain.comment.dto.request.CommentRequest;
import com.backend.domain.comment.dto.response.CommentResponse;
import com.backend.domain.comment.entity.Comment;
import com.backend.domain.fishingtrippost.entity.FishingTripPost;
import com.backend.domain.fishingtrippost.repository.FishingTripPostJpaRepository;
import com.backend.domain.member.entity.Member;
import com.backend.domain.member.repository.MemberJpaRepository;
import com.backend.global.config.QuerydslConfig;
import com.backend.global.dto.request.GlobalRequest;
import com.backend.global.dto.response.ScrollResponse;
import com.backend.global.storage.entity.File;
import com.backend.global.storage.repository.StorageJpaRepository;
import com.backend.global.util.BaseTest;

import jakarta.persistence.EntityManager;
import lombok.extern.slf4j.Slf4j;

import com.navercorp.fixturemonkey.ArbitraryBuilder;

@Slf4j
@Import({CommentQueryRepository.class, QuerydslConfig.class})
@DataJpaTest(includeFilters = @ComponentScan.Filter(type = FilterType.ANNOTATION, classes = Repository.class))
class CommentRepositoryTest extends BaseTest {

	@Autowired
	private CommentRepository commentRepository;

	@Autowired
	private CommentJpaRepository commentJpaRepository;

	@Autowired
	private MemberJpaRepository memberJpaRepository;

	@Autowired
	private StorageJpaRepository storageJpaRepository;

	@Autowired
	private FishingTripPostJpaRepository fishingTripPostJpaRepository;

	@Autowired
	private EntityManager entityManager;

	private List<Member> savedMemberList;

	private List<File> savedfileList;

	private final Arbitrary<String> englishStringLength = Arbitraries.strings()
		.withCharRange('a', 'z')
		.withCharRange('A', 'Z')
		.ofMinLength(1).ofMaxLength(10);

	private final ArbitraryBuilder<Comment> commentArbitraryBuilder = fixtureMonkeyBuilder
		.giveMeBuilder(Comment.class)
		.set("content", englishStringLength);

	@BeforeEach
void setUp() {
    List<File> givenFileList = fixtureMonkeyBuilder
        .giveMeBuilder(File.class)
        .set("fileId", null)
        .sampleList(10);

    savedfileList = storageJpaRepository.saveAll(givenFileList);

    List<Member> memberList = new ArrayList<>();

    for (int i = 0; i < savedfileList.size(); i++) {
		File file = savedfileList.get(i);
        memberList.add(
            fixtureMonkeyBuilder.giveMeBuilder(Member.class)
                .set("memberId", null)
                .set("email", "unique_email_" + i + "@test.com")
                .set("phone", "phone_" + i)
                .set("nickname", "nickname_" + i)
                .set("name", englishStringLength)
                .set("description", englishStringLength)
                .set("fileId", file.getFileId())
				.set("providerId", String.valueOf(i))
                .sample()
        );
    }

    savedMemberList = memberJpaRepository.saveAll(memberList);
}

	@AfterEach
	void afterEach() {
		memberJpaRepository.deleteAll();
		commentJpaRepository.deleteAll();
	}

	@Test
	@DisplayName("댓글 저장 [Repository] - Success")
	void t01() {
		// Given
		Comment givenComment = commentArbitraryBuilder
			.set("commentId", null)
			.sample();

		// When
		Comment savedComment = commentRepository.save(givenComment);

		// Then
		assertThat(savedComment.getCommentId()).isNotNull();
	}

	@Test
	@DisplayName("댓글 존재 여부 [Repository] - Success")
	void t02() {
		// Given
		Comment givenComment = commentArbitraryBuilder
			.set("commentId", null)
			.sample();

		Comment savedComment = commentRepository.save(givenComment);

		// When
		boolean existsByCommentId = commentRepository.existsByCommentId(savedComment.getCommentId());

		// Then
		assertThat(existsByCommentId).isTrue();
	}

	@Test
	@DisplayName("자식 카운트 추가 [Repository] - Success")
	void t03() {
		Comment givenComment = commentArbitraryBuilder
			.set("commentId", null)
			.sample();

		Comment savedComment = commentRepository.save(givenComment);

		// When
		commentRepository.addChildCount(savedComment.getCommentId());

		entityManager.flush();
		entityManager.clear();

		// Then
		Optional<Comment> findComment = commentJpaRepository.findById(savedComment.getCommentId());

		assertThat(findComment.isPresent()).isTrue();
		assertThat(findComment.get().getChildCount()).isNotEqualTo(savedComment.getChildCount());
		assertThat(findComment.get().getChildCount()).isEqualTo(savedComment.getChildCount() + 1);
	}

	@Test
	@DisplayName("댓글 전체 조회 [부모 X] [Repository] - Success")
	public void t04() {
		// Given
		List<Comment> givenCommentList = new ArrayList<>();

		FishingTripPost givenFishingTripPost = fixtureMonkeyBuilder.giveMeBuilder(FishingTripPost.class)
			.set("fishingTripPostId", null)
			.set("subject", englishStringLength)
			.set("content", englishStringLength)
			.sample();

		FishingTripPost savedFishingTripPost = fishingTripPostJpaRepository.save(givenFishingTripPost);

		for (Member member : savedMemberList) {
			givenCommentList.add(fixtureMonkeyBuilder.giveMeBuilder(Comment.class)
				.set("commentId", null)
				.set("content", englishStringLength)
				.set("memberId", member.getMemberId())
				.set("parentId", null)
				.set("childCount", 0)
				.set("fishingTripPostId", savedFishingTripPost.getFishingTripPostId())
				.sample());
		}

		List<Comment> savedCommentList = commentJpaRepository.saveAll(givenCommentList);

		GlobalRequest.CursorRequest givenCursorRequestDto = new GlobalRequest.CursorRequest(null, null, null, null, null, 10);

		CommentRequest.Search givenRequestDto = new CommentRequest.Search(null);

		// When
		ScrollResponse<CommentResponse.Detail> findScrollDetail = commentRepository.findDetailByFishTripPostId(
			savedFishingTripPost.getFishingTripPostId(),
			1L,
			givenCursorRequestDto,
			givenRequestDto
		);

		// Then
		List<Comment> sortedFishEncyclopediaList = savedCommentList.stream()
			.sorted(Comparator.comparing(Comment::getCreatedAt).reversed())
			.toList();

		assertThat(findScrollDetail.content().size()).isEqualTo(sortedFishEncyclopediaList.size());
		assertThat(findScrollDetail.content().get(0).commentId())
			.isEqualTo(sortedFishEncyclopediaList.get(0).getCommentId());
	}

	@Test
	@DisplayName("댓글 전체 조회 [부모 O] [Repository] - Success")
	public void t05() {
		// Given
		List<Comment> givenCommentList = new ArrayList<>();

		FishingTripPost givenFishingTripPost = fixtureMonkeyBuilder.giveMeBuilder(FishingTripPost.class)
			.set("fishingTripPostId", null)
			.set("subject", englishStringLength)
			.set("content", englishStringLength)
			.sample();

		FishingTripPost savedFishingTripPost = fishingTripPostJpaRepository.save(givenFishingTripPost);

		for (Member member : savedMemberList) {
			givenCommentList.add(fixtureMonkeyBuilder.giveMeBuilder(Comment.class)
				.set("commentId", null)
				.set("content", englishStringLength)
				.set("memberId", member.getMemberId())
				.set("parentId", 1L)
				.set("childCount", 0)
				.set("fishingTripPostId", savedFishingTripPost.getFishingTripPostId())
				.sample());

			givenCommentList.add(fixtureMonkeyBuilder.giveMeBuilder(Comment.class)
				.set("commentId", null)
				.set("content", englishStringLength)
				.set("memberId", member.getMemberId())
				.set("parentId", 2L)
				.set("childCount", 0)
				.set("fishingTripPostId", savedFishingTripPost.getFishingTripPostId())
				.sample());
		}

		List<Comment> savedCommentList = commentJpaRepository.saveAll(givenCommentList);

		GlobalRequest.CursorRequest givenCursorRequestDto = new GlobalRequest.CursorRequest(null, null, null, null, null, 30);

		CommentRequest.Search givenRequestDto = new CommentRequest.Search(1L);

		// When
		ScrollResponse<CommentResponse.Detail> findScrollDetail = commentRepository.findDetailByFishTripPostId(
			savedFishingTripPost.getFishingTripPostId(),
			1L,
			givenCursorRequestDto,
			givenRequestDto
		);

		// Then
		List<Comment> sortedFishEncyclopediaList = savedCommentList.stream()
			.filter(comment -> comment.getParentId() == 1)
			.sorted(Comparator.comparing(Comment::getCreatedAt).reversed())
			.toList();

		assertThat(findScrollDetail.content().size()).isEqualTo(sortedFishEncyclopediaList.size());
		assertThat(findScrollDetail.content().get(0).commentId())
			.isEqualTo(sortedFishEncyclopediaList.get(0).getCommentId());
		assertThat(findScrollDetail.content()).allMatch((detail) -> detail.parentId().equals(1L));
	}

	@Test
	@DisplayName("댓글 전체 조회 [부모 O] [Size = 1] [Page = 2] [Repository] - Success")
	public void t06() {
		// Given
		List<Comment> givenCommentList = new ArrayList<>();

		FishingTripPost givenFishingTripPost = fixtureMonkeyBuilder.giveMeBuilder(FishingTripPost.class)
			.set("fishingTripPostId", null)
			.set("subject", englishStringLength)
			.set("content", englishStringLength)
			.sample();

		FishingTripPost savedFishingTripPost = fishingTripPostJpaRepository.save(givenFishingTripPost);

		for (Member member : savedMemberList) {
			givenCommentList.add(fixtureMonkeyBuilder.giveMeBuilder(Comment.class)
				.set("commentId", null)
				.set("content", englishStringLength)
				.set("memberId", member.getMemberId())
				.set("parentId", 1L)
				.set("childCount", 0)
				.set("fishingTripPostId", savedFishingTripPost.getFishingTripPostId())
				.sample());

			givenCommentList.add(fixtureMonkeyBuilder.giveMeBuilder(Comment.class)
				.set("commentId", null)
				.set("content", englishStringLength)
				.set("memberId", member.getMemberId())
				.set("parentId", 2L)
				.set("childCount", 0)
				.set("fishingTripPostId", savedFishingTripPost.getFishingTripPostId())
				.sample());
		}

		List<Comment> savedCommentList = commentJpaRepository.saveAll(givenCommentList);

		Comment getComment = savedCommentList.get(18);

		GlobalRequest.CursorRequest givenCursorRequestDto = new GlobalRequest.CursorRequest(
			null,
			"createdAt",
			null,
			getComment.getCreatedAt().toString(),
			getComment.getCommentId(),
			1);

		CommentRequest.Search givenRequestDto = new CommentRequest.Search(1L);

		// When
		ScrollResponse<CommentResponse.Detail> findScrollDetail = commentRepository.findDetailByFishTripPostId(
			savedFishingTripPost.getFishingTripPostId(),
			1L,
			givenCursorRequestDto,
			givenRequestDto
		);

		// Then
		List<Comment> sortedFishEncyclopediaList = savedCommentList.stream()
			.filter(comment -> comment.getParentId() == 1)
			.sorted(Comparator.comparing(Comment::getCreatedAt).reversed())
			.toList();

		assertThat(findScrollDetail.content().size()).isEqualTo(1);
		assertThat(findScrollDetail.content().get(0).commentId())
			.isEqualTo(sortedFishEncyclopediaList.get(1).getCommentId());
		assertThat(findScrollDetail.content()).allMatch((detail) -> detail.parentId().equals(1L));
	}
}