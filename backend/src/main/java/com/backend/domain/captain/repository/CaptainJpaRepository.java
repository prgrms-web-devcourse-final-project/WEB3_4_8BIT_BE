package com.backend.domain.captain.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.backend.domain.captain.entity.Captain;

public interface CaptainJpaRepository extends JpaRepository<Captain, Long> {

}
