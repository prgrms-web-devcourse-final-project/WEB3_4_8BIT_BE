package com.backend.domain.comment.entity;

import static org.assertj.core.api.Assertions.*;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import com.backend.global.util.BaseTest;

class CommentTest extends BaseTest {

	@Test
	@DisplayName("내용 수정 [Entity] - Success")
	void t01() {
		// Given
		String updateContent = "updateContent";

		Comment givenComment = fixtureMonkeyBuilder.giveMeOne(Comment.class);

		// When
		givenComment.setContent(updateContent);

		// Then
		assertThat(givenComment.getContent()).isEqualTo(updateContent);
	}
}