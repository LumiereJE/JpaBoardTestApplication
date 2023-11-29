package com.kh.jpaboardtest.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.kh.jpaboardtest.dto.ChatMessageDto;
import com.kh.jpaboardtest.dto.ChatRoomResDto;
import com.kh.jpaboardtest.entity.Chat;
import com.kh.jpaboardtest.entity.ChatRoom;
import com.kh.jpaboardtest.repository.ChatRepository;
import com.kh.jpaboardtest.repository.ChatRoomRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketSession;

import javax.annotation.PostConstruct;
import java.io.IOException;
import java.time.LocalDateTime;
import java.util.*;

@Slf4j
@RequiredArgsConstructor
@Service
public class ChatService {
    private final ObjectMapper objectMapper; // JSON 문자열로 변환하기 위한 객체
    private Map<String, ChatRoomResDto> chatRooms; // 채팅방 정보를 담을 맵
    private final ChatRepository chatRepository;
    private final ChatRoomRepository chatRoomRepository;

    @PostConstruct // 의존성 주입 이후 초기화를 수행하는 메소드
    private void init() { // 채팅방 정보를 담을 맵을 초기화
        chatRooms = new LinkedHashMap<>(); // 채팅방 정보를 담을 맵
    }
    public List<ChatRoomResDto> findAllRoom() { // 채팅방 리스트 반환

        return new ArrayList<>(chatRooms.values());
    }

    public ChatRoomResDto findRoomById(String roomId) {
        return chatRooms.get(roomId);
    }

    // 방 개설하기
    public ChatRoomResDto createRoom(String name) {
        String randomId = UUID.randomUUID().toString();
        log.info("UUID : " + randomId);     // 유효한 ID값을 부여하는 부분. 바꿀 수 있음.
        ChatRoomResDto chatRoom = ChatRoomResDto.builder() // 채팅방 생성
                .roomId(randomId)
                .name(name)
                .regDate(LocalDateTime.now())
                .build();
        chatRooms.put(randomId, chatRoom);  // 방 생성, 키를 UUID로 하고 방 정보를 값으로 저장

        ChatRoom newChatRoom = new ChatRoom();
        newChatRoom.setRoomId(chatRoom.getRoomId());
        newChatRoom.setRoomName(chatRoom.getName());
        newChatRoom.setCreatedAt(chatRoom.getRegDate());
        chatRoomRepository.save(newChatRoom);

        return chatRoom;
    }
    public void removeRoom(String roomId) { // 방 삭제
        ChatRoomResDto room = chatRooms.get(roomId); // 방 정보 가져오기
        if (room != null) { // 방이 존재하면
            if (room.isSessionEmpty()) { // 방에 세션이 없으면
                chatRooms.remove(roomId); // 방 삭제
                chatRoomRepository.deleteById(roomId);
            }
        }
    }
    // 채팅방에 입장한 세션 추가
    public void addSessionAndHandleEnter(String roomId, WebSocketSession session, ChatMessageDto chatMessage) {
        ChatRoomResDto room = findRoomById(roomId);
        if (room != null) {
            room.getSessions().add(session); // 채팅방에 입장한 세션 추가
            if (chatMessage.getSender() != null) { // 채팅방에 입장한 사용자가 있으면
                chatMessage.setMessage(chatMessage.getSender() + "님이 입장했습니다.");
                sendMessageToAll(roomId, chatMessage); // 채팅방에 입장 메시지 전송
            }
            log.debug("New session added: " + session);
        }
    }
    // 채팅방에서 퇴장한 세션 제거
    public void removeSessionAndHandleExit(String roomId, WebSocketSession session, ChatMessageDto chatMessage) {
        ChatRoomResDto room = findRoomById(roomId); // 채팅방 정보 가져오기
        if (room != null) {
            room.getSessions().remove(session); // 채팅방에서 퇴장한 세션 제거
            if (chatMessage.getSender() != null) { // 채팅방에서 퇴장한 사용자가 있으면
                chatMessage.setMessage(chatMessage.getSender() + "님이 퇴장했습니다.");
                sendMessageToAll(roomId, chatMessage); // 채팅방에 퇴장 메시지 전송
            }
            log.debug("Session removed: " + session);
            if (room.isSessionEmpty()) {
                removeRoom(roomId);
            }
        }
    }
    // 한명이 채팅쳐도 다른사람에게 보여지도록 함
    public void sendMessageToAll(String roomId, ChatMessageDto message) {
        ChatRoomResDto room = findRoomById(roomId);
        if (room != null) {
            for (WebSocketSession session : room.getSessions()) {
                sendMessage(session, message);
            }
            saveMessage(roomId, message);
        }
    }
    // 메세지를 보내는 작업 자체
    public <T> void sendMessage(WebSocketSession session, T message) {
        try {
            session.sendMessage(new TextMessage(objectMapper.writeValueAsString(message)));
        } catch(IOException e) {
            log.error(e.getMessage(), e);
        }
    }
    // dto -> entity
    public void saveMessage(String roomId, ChatMessageDto chatMessageDto) {
        ChatRoom chatRoom = chatRoomRepository.findById(roomId)
                .orElseThrow(() -> new RuntimeException("해당 채팅방이 존재하지 않습니다."));
        Chat chatMessage = new Chat();
        chatMessage.setChatRoom(chatRoom);
        chatMessage.setSender(chatMessageDto.getSender());
        chatMessage.setMessage(chatMessageDto.getMessage());
        chatMessage.setSentAt(LocalDateTime.now());
        chatMessage.setSenderName(chatMessageDto.getSenderName());
        chatRepository.save(chatMessage);
    }
}