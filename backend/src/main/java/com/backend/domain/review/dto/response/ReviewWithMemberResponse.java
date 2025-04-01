package com.backend.domain.review.dto.response;

import java.time.ZonedDateTime;
import java.util.List;

/**
 * {
 *   "reviewId": 5,
 *   "rating": 5,
 *   "content": "좋은 낚시 경험이었어요!",
 *   "imageList": null,
 *   "memberId": 1,
 *   "nickname": null,
 *   "profileImg": "http://k.kakaocdn.net/UFIzyUGeTtIfTNcu1/img_640x640.jpg",
 *   "createdAt": "2025-03-31T03:41:11.789203Z"
 * }
 *
 * @param reviewId 	리뷰 ID
 * @param rating	별점
 * @param content	리뷰 내용
 * @param imageList	이미지
 * @param shipFishingPostId 선상 낚시 게시글 ID
 * @param memberId	작성자 ID
 * @param nickname	작성자 닉네임
 * @param profileImg	작성자 프로필 이미지
 * @param createdAt	리뷰 작성 일자
 */
public record ReviewWithMemberResponse(
	Long reviewId,
	Integer rating,
	String content,
	List<String> imageList,
	Long shipFishingPostId,
	Long memberId,
	String nickname,
	String profileImg,
	ZonedDateTime createdAt
) {}
