package com.backend.domain.fishingtrippost.notifier;

import java.util.List;

import org.springframework.stereotype.Component;

import com.backend.domain.fishingtrippost.domain.PostStatus;
import com.backend.domain.fishingtrippost.entity.FishingTripPost;
import com.backend.domain.fishingtriprecruitment.repository.FishingTripRecruitmentRepository;
import com.backend.domain.member.repository.MemberRepository;
import com.backend.global.email.service.EmailService;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Component
@RequiredArgsConstructor
public class FishingTripPostNotifier {

	private final FishingTripRecruitmentRepository fishingTripRecruitmentRepository;
	private final MemberRepository memberRepository;
	private final EmailService emailService;

	/**
	 * 모집이 완료된 낚시 게시글에 대해 신청자들에게 안내 메일을 발송합니다.
	 *
	 * <p>게시글의 상태가 {@link PostStatus#COMPLETED}인 경우에만 동작하며,
	 * 해당 게시글에 신청하여 승인된 사용자들의 이메일을 조회한 후,
	 * {@link EmailService#sendFishingTripPostEmail(String, String)} 메서드를 통해
	 * 각 사용자에게 비동기적으로 신청 완료 안내 메일을 전송합니다.</p>
	 *
	 * @param post 상태를 확인하고 메일 발송 대상이 되는 낚시 게시글 엔티티
	 */
	public void notifyMailIfCompleted(final FishingTripPost post) {

		if (post.getPostStatus() != PostStatus.COMPLETED)
			return;

		List<Long> memberIdList = fishingTripRecruitmentRepository
			.findMemberIdListByPostId(post.getFishingTripPostId());
		List<String> emailList = memberRepository.findEmailListByIdList(memberIdList);

		for (String email : emailList) {
			emailService.sendFishingTripPostEmail(post.getSubject(), email);
		}

		log.debug("[동출 게시글] : 메일 전송 완료");
	}
}
