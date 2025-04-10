package com.backend.global.scheduler.entity;

import java.time.ZonedDateTime;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Table(name = "scheduler_status")
@Entity
@Getter
@Builder
@AllArgsConstructor(access = AccessLevel.PRIVATE)
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class SchedulerStatus {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long schedulerId;

	@Column(nullable = false, unique = true)
	private String jobName;

	@Setter
	@Builder.Default
	private ZonedDateTime lastRun = ZonedDateTime.now();
}
