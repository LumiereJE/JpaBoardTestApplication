package com.kh.jpaboardtest.repository;

import com.kh.jpaboardtest.entity.ChatRoom;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ChatRoomRepository extends JpaRepository<ChatRoom, String> {
}
