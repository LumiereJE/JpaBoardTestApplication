package com.kh.jpaboardtest.repository;

import com.kh.jpaboardtest.entity.Chat;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface ChatRepository extends JpaRepository<Chat, Long> {
    @Query(value = "SELECT * FROM chat WHERE rood_id = ? 1 ORDER BY sent_at ASC LIMIT 10", nativeQuery = true)
    List<Chat> findByRoomId(String roomId);
}
