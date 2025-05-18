package com.backend.global.redisson;

import java.lang.reflect.Method;
import java.util.concurrent.TimeUnit;

import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.reflect.MethodSignature;
import org.redisson.api.RLock;
import org.redisson.api.RedissonClient;
import org.springframework.stereotype.Component;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Aspect
@Component
@RequiredArgsConstructor
public class RedissonLockAspect {

	private final RedissonClient redissonClient;

	@Around("@annotation(com.backend.global.redisson.RedissonLock)")
	public Object redissonLock(ProceedingJoinPoint joinPoint) throws Throwable {
		MethodSignature signature = (MethodSignature)joinPoint.getSignature();
		Method method = signature.getMethod();
		RedissonLock annotation = method.getAnnotation(RedissonLock.class);

		String key = String.valueOf(
			CustomSpringELParser.getDynamicKey(
				signature.getParameterNames(),
				joinPoint.getArgs(),
				annotation.key()
			));

		RLock lock = redissonClient.getLock(key);

		try {
			boolean isLocked = lock.tryLock(annotation.waitTime(), annotation.leaseTime(), TimeUnit.MILLISECONDS);
			if (!isLocked) {
				log.warn("락 획득 실패: {}", key);
				return null; // 혹은 예외 던져도 됨
			}
			log.debug("락 획득 성공: {}", key);
			return joinPoint.proceed();
		} finally {
			lock.unlock();
			log.debug("락 해제 완료: {}", key);
		}
	}
}
