package com.backend.domain.comment.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.backend.domain.comment.entity.Comment;

public interface CommentJpaRepository extends JpaRepository<Comment, Long> {

}
