package com.backend.global.storage.entity;

import com.backend.global.baseentity.BaseEntity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;
import lombok.experimental.SuperBuilder;

@Table(name = "files")
@Entity
@Getter
@ToString
@SuperBuilder
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class File extends BaseEntity {

	@Id
	@Column(name = "file_id")
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long fileId;

	@Column(nullable = false)
	private String fileName;

	@Column(nullable = false)
	private String originalFileName;

	@Column(nullable = false)
	private String contentType;

	@Column(nullable = false)
	private Long fileSize;

	@Column
	private String url;

	@Column(nullable = false)
	private String domain;

	@Column(nullable = false)
	private Long createdById;

	@Column(nullable = false)
	private Boolean uploaded = false;

	public void completeUpload() {
		this.uploaded = true;
	}
}
