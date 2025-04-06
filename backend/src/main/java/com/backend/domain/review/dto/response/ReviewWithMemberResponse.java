package com.backend.domain.review.dto.response;

import java.time.ZonedDateTime;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonInclude;

/**
 * {
 *   "reviewId": 5,
 *   "rating": 5,
 *   "content": "좋은 낚시 경험이었어요!",
 *   "fileUrlList": ["reviewImage1.jpg", "reviewImage2.png"],
 *   "memberId": 1,
 *   "nickname": 강태공,
 *   "profileImg": "profileImage1.jpg",
 *   "isAuthor": true,
 *   "createdAt": "2025-03-31T03:41:11.789203Z"
 * }
 *
 * @param reviewId 	리뷰 ID
 * @param rating	별점
 * @param content	리뷰 내용
 * @param fileUrlList	이미지 URL 리스트
 * @param shipFishingPostId 선상 낚시 게시글 ID
 * @param memberId	작성자 ID -> 작성자가 아니면 제외
 * @param nickname	작성자 닉네임
 * @param profileImg	작성자 프로필 이미지
 * @param isAuthor	작성자 확인 값
 * @param createdAt	리뷰 작성 일자
 */
@JsonInclude(JsonInclude.Include.NON_NULL)
public record ReviewWithMemberResponse(
	Long reviewId,
	Integer rating,
	String content,
	List<String> fileUrlList,
	Long shipFishingPostId,
	Long memberId,
	String nickname,
	String profileImg,
	Boolean isAuthor,
	ZonedDateTime createdAt
) {}
