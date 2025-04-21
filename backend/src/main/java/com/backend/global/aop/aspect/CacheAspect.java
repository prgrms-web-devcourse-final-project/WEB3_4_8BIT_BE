package com.backend.global.aop.aspect;

import java.lang.reflect.Method;

import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.reflect.MethodSignature;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.expression.Expression;
import org.springframework.expression.ExpressionParser;
import org.springframework.expression.spel.standard.SpelExpressionParser;
import org.springframework.expression.spel.support.StandardEvaluationContext;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;

import com.backend.global.aop.annotation.CacheDelete;
import com.backend.global.aop.annotation.CustomCache;
import com.backend.global.auth.oauth2.CustomOAuth2User;
import com.fasterxml.jackson.databind.ObjectMapper;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Aspect
@Component
@RequiredArgsConstructor
public class CacheAspect {

	private final ObjectMapper objectMapper;
	private final RedisTemplate<String, Object> redisTemplate;

	/**
	 * 메서드 반환 값 캐시 어노테이션 메서드
	 *
	 * @param joinPoint 감싸진 메서드
	 * @param customCache 어노테이션
	 * @return 캐시된 데이터
	 * @throws Throwable 예외
	 */
	@Around("@annotation(customCache)")
	public Object around(final ProceedingJoinPoint joinPoint, final CustomCache customCache) throws Throwable {
		String cacheKey = generateKey(customCache.prefix(), customCache.key(), customCache.id(),
			customCache.useMember(), joinPoint);

		Object result = null;

		try {
			Object cachedData = redisTemplate.opsForValue().get(cacheKey);

			if (cachedData != null) {
				proceedLogMessage(cachedData.toString());

				Class<?> returnType = getMethodReturnType(joinPoint);

				return objectMapper.convertValue(cachedData, returnType);
			}

			result = joinPoint.proceed();

			redisTemplate.opsForValue().set(cacheKey, result, customCache.ttl(), customCache.ttlUnit());

			return result;
		} catch (Exception e) {
			errorLogMessage(e.getMessage());

			return joinPoint.proceed();
		}
	}

	/**
	 * 캐시 된 데이터 삭제 어노테이션 메서드
	 *
	 * @param joinPoint 감싸진 메서드
	 * @param cacheDelete 어노테이션
	 * @return 감싸진 메서드 실행
	 * @throws Throwable 예외
	 */
	@Around("@annotation(cacheDelete)")
	public Object around(final ProceedingJoinPoint joinPoint, final CacheDelete cacheDelete) throws Throwable {
		String cacheKey = generateKey(cacheDelete.prefix(), cacheDelete.key(), cacheDelete.id(),
			cacheDelete.useMember(), joinPoint);

		try {
			if (redisTemplate.hasKey(cacheKey)) {
				proceedLogMessage(cacheKey);

				redisTemplate.delete(cacheKey);
			}

			return joinPoint.proceed();
		} catch (Exception e) {
			errorLogMessage(e.getMessage());

			return joinPoint.proceed();
		}
	}

	/**
	 * 입력된 정보를 기반으로 redis 에서 사용할 key 생성 메서드
	 *
	 * @param prefix 도메인 종류
	 * @param key 캐시 키 주제 ex) shipFishingPostId
	 * @param idSpel id param 명 #name
	 * @return 캐시 키 값
	 */
	private String generateKey(final String prefix, final String key, final String idSpel, final boolean useMember,
		final ProceedingJoinPoint joinPoint) {
		Long memberId = getMemberId();
		Long id = resolveId(idSpel, joinPoint);

		return useMember ? String.format("%s:%s:%d:memberId:%d", prefix, key, id, memberId) :
			String.format("%s:%s:%d", prefix, key, id);

	}

	/**
	 * 현재 로그인 한 유저의 ID를 가져오는 메서드
	 *
	 * @return memberId
	 */
	private Long getMemberId() {
		Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

		if (authentication == null || !authentication.isAuthenticated()) {

			return null;
		}

		CustomOAuth2User user = (CustomOAuth2User)authentication.getPrincipal();

		return user.getId();
	}

	/**
	 * 메서드 반환 타입 정보 추출 메서드
	 *
	 * @param joinPoint 실행 시점
	 * @return 메서드 반환 타입
	 */
	private Class<?> getMethodReturnType(ProceedingJoinPoint joinPoint) {
		MethodSignature methodSignature = (MethodSignature)joinPoint.getSignature();
		Method method = methodSignature.getMethod();

		return method.getReturnType();
	}

	/**
	 * SpEL 표현식을 파싱하여 Long 타입 ID 값으로 변환하는 메서드
	 *
	 * @param spel
	 * @param joinPoint
	 * @return
	 */
	private Long resolveId(String spel, ProceedingJoinPoint joinPoint) {
		try {
			Object parsed = parseSpel(spel, joinPoint);
			return (parsed instanceof Number)
				? ((Number)parsed).longValue()
				: Long.parseLong(String.valueOf(parsed));
		} catch (Exception e) {
			log.warn("SpEL 파싱 실패: {}, fallback to -1", spel);
			return 0L;
		}
	}

	/**
	 * SpEL 문자열을 메서드 인자 값으로 변환하는 메서드
	 *
	 * @param spel
	 * @param joinPoint
	 * @return
	 */
	private Object parseSpel(String spel, ProceedingJoinPoint joinPoint) {
		if (!spel.startsWith("#")) {
			return spel;
		}

		ExpressionParser parser = new SpelExpressionParser();
		Expression expression = parser.parseExpression(spel);

		// 파라미터 이름 추출
		MethodSignature methodSignature = (MethodSignature)joinPoint.getSignature();
		String[] parameterNames = methodSignature.getParameterNames();
		Object[] args = joinPoint.getArgs();

		// context 생성
		StandardEvaluationContext context = new StandardEvaluationContext();
		for (int i = 0; i < parameterNames.length; i++) {
			context.setVariable(parameterNames[i], args[i]);
		}

		return expression.getValue(context);
	}

	/**
	 * 레디스 데이터 조회 메서드
	 *
	 * @param message 진행 메세지
	 */
	private void proceedLogMessage(final String message) {
		log.debug("redis 캐시 데이터를 조회합니다 : {}", message);
	}

	/**
	 * 레디스 서버에 문제가 발생했을때 기록되는 로그 메세지 공통화 메서드
	 *
	 * @param message 에러 메세지
	 */
	private void errorLogMessage(final String message) {
		log.debug("redis 캐시 과정에서 문제가 발생했습니다 : {}", message);
	}
}
