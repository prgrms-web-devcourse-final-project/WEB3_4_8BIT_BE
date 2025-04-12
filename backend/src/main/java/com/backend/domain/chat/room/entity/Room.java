package com.backend.domain.chat.room.entity;

import java.time.ZonedDateTime;

import com.backend.global.baseentity.BaseEntity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import jakarta.persistence.UniqueConstraint;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

@Table(
	name = "rooms",
	uniqueConstraints = {
		@UniqueConstraint(
			name = "uk_room_target_id_type",
			columnNames = {"target_id", "target_type"}
		)
	}
)
@Entity
@Getter
@SuperBuilder
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Room extends BaseEntity {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long roomId;

	@Column(nullable = false)
	private Long targetId;

	@Column(nullable = false)
	private String targetName;

	@Enumerated(EnumType.STRING)
	@Column(nullable = false)
	private TargetType targetType;

	@Enumerated(EnumType.STRING)
	@Column(nullable = false)
	private Status status;

	private ZonedDateTime lastMessageTime;
}
