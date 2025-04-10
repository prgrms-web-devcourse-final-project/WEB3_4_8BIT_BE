package com.backend.domain.chat.room.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.backend.domain.chat.room.entity.Room;

public interface RoomJpaRepository extends JpaRepository<Room, Long> {
}
