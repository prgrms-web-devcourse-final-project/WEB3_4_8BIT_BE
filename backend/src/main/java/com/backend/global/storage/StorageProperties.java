package com.backend.global.storage;

import org.springframework.boot.context.properties.ConfigurationProperties;

import lombok.Getter;

@Getter
@ConfigurationProperties(prefix = "aws.s3")
public class StorageProperties {

	private String bucketName;
	private String region;
	private String accessKey;
	private String secretKey;
	private String baseUrl;

	public String buildAccessUrl(String fileName) {
		return baseUrl + "/" + fileName;
	}
}