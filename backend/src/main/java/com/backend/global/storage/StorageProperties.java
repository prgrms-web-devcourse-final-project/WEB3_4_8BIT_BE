package com.backend.global.storage;

import org.springframework.boot.context.properties.ConfigurationProperties;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
@ConfigurationProperties(prefix = "aws.s3")
public class StorageProperties {

	private final String bucketName;
	private final String region;
	private final String accessKey;
	private final String secretKey;
	private final String baseUrl;

	public String buildAccessUrl(String fileName) {
		return baseUrl + "/" + fileName;
	}
}