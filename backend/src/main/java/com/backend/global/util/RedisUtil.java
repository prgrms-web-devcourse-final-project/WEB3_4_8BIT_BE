package com.backend.global.util;

import java.nio.charset.StandardCharsets;
import java.time.Duration;
import java.util.HashMap;
import java.util.Map;

import org.springframework.data.redis.core.RedisCallback;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.ScanOptions;
import org.springframework.data.redis.core.ValueOperations;
import org.springframework.stereotype.Component;

import lombok.RequiredArgsConstructor;

/**
 * Redis 관련 유틸리티 클래스
 */
@Component
@RequiredArgsConstructor
public class RedisUtil {

	private final RedisTemplate<String, String> redisTemplate;

	/**
	 * 지정된 key 값을 1 증가시킨다.
	 *
	 * @param key Redis 저장된 key
	 */
	public void increment(final String key) {
		redisTemplate.opsForValue().increment(key);
	}

	/**
	 * 지정된 key 값을 1 감소시킨다.
	 *
	 * @param key Redis 저장된 key
	 */
	public void decrement(final String key) {
		redisTemplate.opsForValue().decrement(key);
	}

	/**
	 * 해당 키가 Redis 존재 여부 확인
	 *
	 * @param key Redis 키
	 * @return 존재하면 true, 없으면 false
	 */
	public boolean hasKey(final String key) {
		return redisTemplate.hasKey(key);
	}

	/**
	 * Redis 해당 키값 value 세팅
	 *
	 * @param key   Redis 키
	 * @param value 키에 넣을 값
	 */
	public void setValue(final String key, final String value) {
		redisTemplate.opsForValue().set(key, value, Duration.ofMinutes(10));
	}

	/**
	 * Redis 해당 키값 value get
	 *
	 * @param key Redis 키
	 * @return value 값
	 */
	public String getValue(final String key) {
		return redisTemplate.opsForValue().get(key);
	}

	/**
	 * key Redis 존재시 삭제
	 *
	 * @param key 삭제할 Redis key
	 */
	public void deleteKeyIfExists(final String key) {
		if (redisTemplate.hasKey(key)) {
			redisTemplate.delete(key);
		}
	}

	/**
	 * 주어진 prefix로 시작하는 Redis key들을 SCAN 명령으로 검색하고
	 * 해당 key들의 value를 함께 Map으로 반환한다.
	 *
	 * <p><b>주의:</b> 이 메서드는 Spring Data Redis 3.x 기준으로
	 * {@code scan(ScanOptions)} 메서드가 deprecated 되었지만,
	 * 대체 API가 명확하지 않고 성능 상 점진적 탐색이 필요한 경우
	 * 여전히 실무에서 사용되는 방식입니다.
	 * 추후 Spring에서 제거될 수 있으므로 유지보수 시 참고하세요.
	 *
	 * @param prefix Redis key prefix (예: like_count::)
	 * @return key-value 쌍을 담은 Map
	 */

	public Map<String, Long> scanKeysAndValues(final String prefix) {
		Map<String, Long> result = new HashMap<>();
		ValueOperations<String, String> ops = redisTemplate.opsForValue();

		redisTemplate.execute((RedisCallback<Void>)connection -> {
			ScanOptions options = ScanOptions.scanOptions().match(prefix + "*").count(1000).build();
			try (var cursor = connection.scan(options)) {
				cursor.forEachRemaining(rawKey -> {
					String key = new String(rawKey, StandardCharsets.UTF_8);
					String value = ops.get(key);
					if (value != null) {
						result.put(key, Long.parseLong(value));
					}
				});
			} catch (Exception e) {
				throw new RuntimeException("Redis scan failed", e);
			}
			return null;
		});

		return result;
	}
}