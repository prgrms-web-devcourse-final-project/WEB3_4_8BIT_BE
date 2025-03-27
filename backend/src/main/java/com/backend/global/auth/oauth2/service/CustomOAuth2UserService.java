package com.backend.global.auth.oauth2.service;

import java.util.Collections;
import java.util.Optional;

import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.oauth2.client.userinfo.DefaultOAuth2UserService;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserRequest;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserService;
import org.springframework.security.oauth2.core.OAuth2AuthenticationException;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.backend.domain.member.domain.MemberRole;
import com.backend.domain.member.domain.Provider;
import com.backend.domain.member.entity.Members;
import com.backend.domain.member.exception.MembersErrorCode;
import com.backend.domain.member.exception.MembersException;
import com.backend.domain.member.repository.MembersRepository;
import com.backend.global.auth.oauth2.CustomOAuth2User;
import com.backend.global.auth.oauth2.userinfo.KakaoOAuth2UserInfo;
import com.backend.global.auth.oauth2.userinfo.NaverOauth2UserInfo;
import com.backend.global.auth.oauth2.userinfo.OAuth2UserInfo;
import com.backend.global.exception.GlobalException;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
@RequiredArgsConstructor
public class CustomOAuth2UserService implements OAuth2UserService<OAuth2UserRequest, OAuth2User> {

	private final MembersRepository membersRepository;

	@Override
	@Transactional
	public OAuth2User loadUser(OAuth2UserRequest userRequest) throws OAuth2AuthenticationException {
		OAuth2UserService<OAuth2UserRequest, OAuth2User> delegate = new DefaultOAuth2UserService();
		OAuth2User oAuth2User = delegate.loadUser(userRequest);

		// 현재 로그인 시도 중인 OAuth2 서비스의 식별자 (ex: "kakao", "naver")
		String registrationId = userRequest.getClientRegistration().getRegistrationId();
		log.debug("Provider: {}, OAuth2User attributes: {}", registrationId,oAuth2User.getAttributes());

		// 사용자 정보를 식별하기 위한 OAuth2의 기본 키 필드명 (ex: "id", "sub")
		String usernameAttributeName = userRequest.getClientRegistration()
			.getProviderDetails()
			.getUserInfoEndpoint()
			.getUserNameAttributeName();
		log.debug("Username attribute name: {}", usernameAttributeName);

		// OAuth2UserInfo 객체 생성
		OAuth2UserInfo userInfo = switch (registrationId.toLowerCase()) {
			case "naver" -> new NaverOauth2UserInfo(oAuth2User.getAttributes());
			case "kakao" -> new KakaoOAuth2UserInfo(oAuth2User.getAttributes());
			default -> throw new MembersException(MembersErrorCode.UNSUPPORTED_PROVIDER);
		};

		// 유저 정보 저장 또는 업데이트
		Members member = saveOrUpdate(userInfo, registrationId);
		log.debug("Saved/Updated member - id: {}, email: {}", member.getMemberId(), member.getEmail());

		return new CustomOAuth2User(
			Collections.singleton(new SimpleGrantedAuthority(member.getRole().name())),
			oAuth2User.getAttributes(),
			usernameAttributeName,
			member.getMemberId(),
			member.getEmail()
		);
	}

	private Members saveOrUpdate(OAuth2UserInfo userInfo, String registrationId) {
		// 전화번호 포맷팅 (카카오의 경우)
		String formattedPhone = registrationId.equalsIgnoreCase("kakao")
			? formatPhoneNumber(userInfo.getPhoneNumber())
			: userInfo.getPhoneNumber();

		Provider provider = getProvider(registrationId);

		Optional<Members> optionalMember = membersRepository
			.findByPhone(formattedPhone);

		if (optionalMember.isPresent()) {
			Members member = optionalMember.get();

			// 현재 로그인 시도한 provider가 다르면 예외
			if (!member.getProvider().equals(provider)) {
				log.warn("같은 전화번호로 다른 플랫폼 가입 시도! 기존: {}, 시도: {}", member.getProvider(), provider);
				throw new MembersException(MembersErrorCode.PROVIDER_CONFLICT);
			}

			// 같은 플랫폼이면 기존 유저 정보 업데이트
			member.updateUserProfile(
				userInfo.getName(),
				userInfo.getImageUrl()
			);
			return member;
		} else {
			// 신규 회원 가입
			Members member = Members.builder()
				.email(userInfo.getEmail())
				.name(userInfo.getName())
				.profileImg(userInfo.getImageUrl())
				.phone(formattedPhone)
				.providerId(userInfo.getId())
				.provider(provider)
				.isAddInfo(false)
				.role(MemberRole.USER)
				.build();
			return membersRepository.save(member);
		}
	}

	/**
	 * registrationId를 Provider Enum 처리
	 *
	 * @param registrationId OAuth2 서비스 구분 ID (ex: kakao, google)
	 * @throws MembersException 지원하지 않는 OAuth2 Provider
	 */
	private Provider getProvider(String registrationId) {
		try {
			return Provider.valueOf(registrationId.toUpperCase());
		} catch (Exception e) {
			throw new MembersException(MembersErrorCode.UNSUPPORTED_PROVIDER);
		}
	}

	/**
	 * 카카오에서 받아온 전화번호를 한국 휴대폰 형식으로 변환하는 유틸 메서드.
	 * 예: "+82 10-1234-5678" → "010-1234-5678"
	 *
	 * @param rawPhone 카카오에서 전달받은 원본 전화번호 문자열
	 * @return 포맷팅된 전화번호 (ex: 010-1234-5678), 값이 없을 경우 null 반환
	 */
	private String formatPhoneNumber(String rawPhone) {
		if (rawPhone == null)
			return null;

		// 국가번호 +82를 0으로 대체하고, 앞뒤 공백 제거
		return rawPhone.replace("+82 ", "0").trim();
	}

}
