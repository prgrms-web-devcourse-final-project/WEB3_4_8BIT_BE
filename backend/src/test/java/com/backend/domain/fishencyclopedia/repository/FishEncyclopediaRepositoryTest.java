package com.backend.domain.fishencyclopedia.repository;

import static org.assertj.core.api.Assertions.*;

import java.util.Comparator;
import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.context.annotation.Import;
import org.springframework.data.domain.Slice;

import net.jqwik.api.Arbitraries;
import net.jqwik.api.Arbitrary;

import com.backend.domain.fish.entity.Fish;
import com.backend.domain.fish.repository.FishJpaRepository;
import com.backend.domain.fishencyclopedia.dto.request.FishEncyclopediaRequest;
import com.backend.domain.fishencyclopedia.dto.response.FishEncyclopediaResponse;
import com.backend.domain.fishencyclopedia.entity.FishEncyclopedia;
import com.backend.domain.fishpoint.entity.FishPoint;
import com.backend.domain.fishpoint.repository.FishPointJpaRepository;
import com.backend.global.config.JpaAuditingConfig;
import com.backend.global.config.QuerydslConfig;
import com.backend.global.util.BaseTest;

import lombok.extern.slf4j.Slf4j;

import com.navercorp.fixturemonkey.ArbitraryBuilder;

@Import({FishEncyclopediaRepositoryImpl.class, FishEncyclopediaQueryRepository.class, JpaAuditingConfig.class,
	QuerydslConfig.class})
@DataJpaTest
@Slf4j
class FishEncyclopediaRepositoryTest extends BaseTest {

	@Autowired
	private FishEncyclopediaRepository fishEncyclopediaRepository;

	@Autowired
	private FishEncyclopediaJpaRepository fishEncyclopediaJpaRepository;

	@Autowired
	private FishEncyclopediaQueryRepository fishEncyclopediaQueryRepository;

	@Autowired
	private FishPointJpaRepository fishPointJpaRepository;

	@Autowired
	private FishJpaRepository fishJpaRepository;

	    private List<FishPoint> savedFishPointList;
    private List<Fish> savedFishList;
    private List<FishEncyclopedia> savedFishEncyclopediasList1;


	final Arbitrary<String> englishStringLength = Arbitraries.strings()
		.withCharRange('a', 'z')
		.withCharRange('A', 'Z')
		.ofMinLength(1).ofMaxLength(30);

	final ArbitraryBuilder<FishPoint> fishPointarbitraryBuilder = fixtureMonkeyBuilder.giveMeBuilder(FishPoint.class)
		.set("fishPointName", englishStringLength)
		.set("fishPointDetailName", englishStringLength);

	final ArbitraryBuilder<Fish> fishArbitraryBuilder = fixtureMonkeyBuilder.giveMeBuilder(Fish.class)
		.set("name", englishStringLength)
		.set("icon", englishStringLength)
		.set("spawnLocation", englishStringLength);

	@BeforeEach
    void setUp() {
        // 공통 테스트 데이터 설정
        List<FishPoint> givenFishPointList = fishPointarbitraryBuilder
                .set("fishPointId", null)
                .sampleList(2);
        savedFishPointList = fishPointJpaRepository.saveAll(givenFishPointList);

        List<Fish> givenFishList = fishArbitraryBuilder
                .set("fishId", null)
                .sampleList(2);
        savedFishList = fishJpaRepository.saveAll(givenFishList);

        List<FishEncyclopedia> fishEncyclopediasList1 = fixtureMonkeyBuilder
                .giveMeBuilder(FishEncyclopedia.class)
                .set("fishEncyclopediaId", null)
                .set("fishId", savedFishList.get(0).getFishId())
                .set("fishPointId", savedFishPointList.get(1).getFishPointId())
                .sampleList(7);

        List<FishEncyclopedia> fishEncyclopediasList2 = fixtureMonkeyBuilder
                .giveMeBuilder(FishEncyclopedia.class)
                .set("fishEncyclopediaId", null)
                .set("fishId", savedFishList.get(1).getFishId())
                .set("fishPointId", savedFishPointList.get(0).getFishPointId())
                .sampleList(10);

        savedFishEncyclopediasList1 = fishEncyclopediaJpaRepository.saveAll(fishEncyclopediasList1);
        fishEncyclopediaJpaRepository.saveAll(fishEncyclopediasList2);
    }

	@Test
	@DisplayName("물고기 도감 저장 [Repository] - Success")
	void t01() {
		// Given
		FishEncyclopedia givenFishEncyclopedia = fixtureMonkeyBuilder
			.giveMeBuilder(FishEncyclopedia.class)
			.set("fishEncyclopediaId", null)
			.sample();

		// When
		FishEncyclopedia savedFishEncyclopedia = fishEncyclopediaRepository.createFishEncyclopedia(
			givenFishEncyclopedia);

		// Then
		assertThat(savedFishEncyclopedia.getFishEncyclopediaId()).isNotNull();
	}

	@Test
    @DisplayName("물고기 상세 조회 [Default] [Repository] - Success")
    void t02() {
        // Given
        FishEncyclopediaRequest.PageRequest givenRequestDto = new FishEncyclopediaRequest.PageRequest(
                null, null, null, null
        );

        // When
        Slice<FishEncyclopediaResponse.Detail> savedFishEncyclopedia = executeQuery(givenRequestDto);

        // Then
        List<FishEncyclopediaResponse.Detail> content = savedFishEncyclopedia.getContent();

		List<FishEncyclopedia> sortedFishEncyclopediaList = savedFishEncyclopediasList1.stream()
			.sorted(Comparator.comparing(FishEncyclopedia::getCreatedAt).reversed())
			.toList();

		assertThat(savedFishEncyclopedia).hasSize(7);
		assertThat(content.get(0).createdAt()).isEqualTo(sortedFishEncyclopediaList.get(0).getCreatedAt());
    }

    @Test
    @DisplayName("물고기 상세 조회 [Sort - Default] [Order - Asc] [Repository] - Success")
    void t03() {
        // Given
        FishEncyclopediaRequest.PageRequest givenRequestDto = new FishEncyclopediaRequest.PageRequest(
                null, null, null, "ASC"
        );

        // When
        Slice<FishEncyclopediaResponse.Detail> savedFishEncyclopedia = executeQuery(givenRequestDto);

        // Then
        List<FishEncyclopediaResponse.Detail> content = savedFishEncyclopedia.getContent();

		List<FishEncyclopedia> sortedFishEncyclopediaList = savedFishEncyclopediasList1.stream()
			.sorted(Comparator.comparing(FishEncyclopedia::getCreatedAt))
			.toList();

		assertThat(savedFishEncyclopedia).hasSize(7);
		assertThat(content.get(0).createdAt()).isEqualTo(sortedFishEncyclopediaList.get(0).getCreatedAt());
    }

    @Test
    @DisplayName("물고기 상세 조회 [Sort - Length] [Order - Default] [Repository] - Success")
    void t04() {
        // Given
        FishEncyclopediaRequest.PageRequest givenRequestDto = new FishEncyclopediaRequest.PageRequest(
                null, null, "length", null
        );

        // When
        Slice<FishEncyclopediaResponse.Detail> savedFishEncyclopedia = executeQuery(givenRequestDto);

        // Then
        List<FishEncyclopediaResponse.Detail> content = savedFishEncyclopedia.getContent();

		List<FishEncyclopedia> sortedFishEncyclopediaList = savedFishEncyclopediasList1.stream()
			.sorted(Comparator.comparing(FishEncyclopedia::getLength).reversed())
			.toList();

		assertThat(savedFishEncyclopedia).hasSize(7);
		assertThat(content.get(0).length()).isEqualTo(sortedFishEncyclopediaList.get(0).getLength());
    }

    @Test
    @DisplayName("물고기 상세 조회 [Sort - Length] [Order - ASC] [Repository] - Success")
    void t05() {
        // Given
        FishEncyclopediaRequest.PageRequest givenRequestDto = new FishEncyclopediaRequest.PageRequest(
                null, null, "length", "asc"
        );

        // When
        Slice<FishEncyclopediaResponse.Detail> savedFishEncyclopedia = executeQuery(givenRequestDto);

        // Then
        List<FishEncyclopedia> sortedFishEncyclopediaList = savedFishEncyclopediasList1.stream()
			.sorted(Comparator.comparing(FishEncyclopedia::getLength))
			.toList();

		List<FishEncyclopediaResponse.Detail> content = savedFishEncyclopedia.getContent();

		assertThat(savedFishEncyclopedia).hasSize(7);
		assertThat(content.get(0).length()).isEqualTo(sortedFishEncyclopediaList.get(0).getLength());
    }

    @Test
    @DisplayName("물고기 상세 조회 [Sort - Count] [Order - Default] [Repository] - Success")
    void t06() {
        // Given
        FishEncyclopediaRequest.PageRequest givenRequestDto = new FishEncyclopediaRequest.PageRequest(
                null, null, "count", null
        );

        // When
        Slice<FishEncyclopediaResponse.Detail> savedFishEncyclopedia = executeQuery(givenRequestDto);

        // Then
        List<FishEncyclopedia> sortedFishEncyclopediaList = savedFishEncyclopediasList1.stream()
			.sorted(Comparator.comparing(FishEncyclopedia::getCount).reversed())
			.toList();

		List<FishEncyclopediaResponse.Detail> content = savedFishEncyclopedia.getContent();

		assertThat(savedFishEncyclopedia).hasSize(7);
		assertThat(content.get(0).count()).isEqualTo(sortedFishEncyclopediaList.get(0).getCount());
    }

	@Test
    @DisplayName("물고기 상세 조회 [Sort - Count] [Order - Asc] [Repository] - Success")
    void t07() {
        // Given
        FishEncyclopediaRequest.PageRequest givenRequestDto = new FishEncyclopediaRequest.PageRequest(
                null, null, "count", "asc"
        );

        // When
        Slice<FishEncyclopediaResponse.Detail> savedFishEncyclopedia = executeQuery(givenRequestDto);

        // Then
        List<FishEncyclopedia> sortedFishEncyclopediaList = savedFishEncyclopediasList1.stream()
			.sorted(Comparator.comparing(FishEncyclopedia::getCount))
			.toList();

		List<FishEncyclopediaResponse.Detail> content = savedFishEncyclopedia.getContent();

		assertThat(savedFishEncyclopedia).hasSize(7);
		assertThat(content.get(0).count()).isEqualTo(sortedFishEncyclopediaList.get(0).getCount());
    }

    // 유틸리티 메서드
    private Slice<FishEncyclopediaResponse.Detail> executeQuery(FishEncyclopediaRequest.PageRequest requestDto) {
        return fishEncyclopediaQueryRepository.findDetailByAllByFishPointIdAndFishId(
                requestDto,
                savedFishPointList.get(1).getFishPointId(),
                savedFishList.get(0).getFishId()
        );
    }

}