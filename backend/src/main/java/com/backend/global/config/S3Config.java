package com.backend.global.config;

import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import com.backend.global.storage.StorageProperties;

import software.amazon.awssdk.auth.credentials.AwsBasicCredentials;
import software.amazon.awssdk.auth.credentials.StaticCredentialsProvider;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.presigner.S3Presigner;

@Configuration
@EnableConfigurationProperties(StorageProperties.class)
public class S3Config {

	@Bean
	public S3Presigner s3Presigner(StorageProperties properties) {
		return S3Presigner.builder()
			.region(Region.of(properties.getRegion()))
			.credentialsProvider(StaticCredentialsProvider.create(
				AwsBasicCredentials.create(properties.getAccessKey(), properties.getSecretKey())
			))
			.build();
	}

	@Bean
	public S3Client s3Client(StorageProperties properties) {
		return S3Client.builder()
			.region(Region.of(properties.getRegion()))
			.credentialsProvider(StaticCredentialsProvider.create(
				AwsBasicCredentials.create(properties.getAccessKey(), properties.getSecretKey())
			))
			.build();
	}
}