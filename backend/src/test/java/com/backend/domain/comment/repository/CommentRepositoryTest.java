package com.backend.domain.comment.repository;

import static org.assertj.core.api.Assertions.*;

import java.util.Optional;

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

import com.backend.domain.comment.entity.Comment;
import com.backend.global.config.QuerydslConfig;
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
	private EntityManager entityManager;

	private final Arbitrary<String> englishStringLength = Arbitraries.strings()
		.withCharRange('a', 'z')
		.withCharRange('A', 'Z')
		.ofMinLength(1).ofMaxLength(10);

	private final ArbitraryBuilder<Comment> commentArbitraryBuilder = fixtureMonkeyBuilder
		.giveMeBuilder(Comment.class)
		.set("content", englishStringLength);

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
}