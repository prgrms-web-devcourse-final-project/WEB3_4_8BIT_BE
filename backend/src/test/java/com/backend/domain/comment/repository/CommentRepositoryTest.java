package com.backend.domain.comment.repository;

import static org.assertj.core.api.Assertions.*;

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
}