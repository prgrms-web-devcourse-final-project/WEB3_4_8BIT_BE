package com.backend.domain.like.service;

import static org.junit.jupiter.api.Assertions.*;

import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.atomic.AtomicInteger;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import com.backend.domain.like.domain.LikeTargetType;
import com.backend.global.util.RedisUtil;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@SpringBootTest
class LikeCacheServiceTest {

	@Autowired
	private LikeCacheService likeCacheService;

	@Autowired
	private RedisUtil redisUtil;

	private final LikeTargetType TYPE = LikeTargetType.FISHING_TRIP_POST;
	private final Long TARGET_ID = 1L;

	private String redisKey;

	@BeforeEach
	void setUp() {
		redisKey = "like_count::" + TYPE.name() + "::" + TARGET_ID;
		redisUtil.setValue(redisKey, "0");
	}

	@AfterEach
	void tearDown() {
		redisUtil.deleteKeyIfExists(redisKey);
	}

	private void runConcurrentTest(Runnable incTask, Runnable decTask) throws InterruptedException {
		ExecutorService executor = Executors.newFixedThreadPool(32);
		CountDownLatch latch = new CountDownLatch(100);

		for (int i = 0; i < 50; i++) {
			executor.submit(() -> {
				try {
					incTask.run();
				} finally {
					latch.countDown();
				}
			});
		}

		for (int i = 0; i < 50; i++) {
			executor.submit(() -> {
				try {
					decTask.run();
				} finally {
					latch.countDown();
				}
			});
		}

		latch.await();
	}

	@Test
	@DisplayName("Redisson Lock 적용 [increase:50 decrease:50]")
	void testWithRedissonLock() throws InterruptedException {
		AtomicInteger incCounter = new AtomicInteger();
		AtomicInteger decCounter = new AtomicInteger();

		runConcurrentTest(
			() -> {
				likeCacheService.updateLikeCountCache(TYPE, TARGET_ID, true);
				incCounter.incrementAndGet();
			},
			() -> {
				likeCacheService.updateLikeCountCache(TYPE, TARGET_ID, false);
				decCounter.incrementAndGet();
			}
		);

		Long finalCount = Long.parseLong(redisUtil.getValue(redisKey));
		long expected = 0L;

		log.debug("\n" +
				"====================== [LOCK 적용 결과] ======================\n" +
				"기대값          : {}\n" +
				"실제값          : {}\n" +
				"증가 호출 수    : {}\n" +
				"감소 호출 수    : {}\n" +
				"===============================================================",
			expected, finalCount, incCounter.get(), decCounter.get());

		assertEquals(expected, finalCount);
	}
}
