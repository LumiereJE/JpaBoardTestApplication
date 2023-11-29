package com.kh.jpaboardtest.controller;


import com.kh.jpaboardtest.dto.ChatRoomReqDto;
import com.kh.jpaboardtest.dto.ChatRoomResDto;
import com.kh.jpaboardtest.service.ChatService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

import static com.kh.jpaboardtest.utils.Common.CORS_ORIGIN;

@Slf4j
@RequiredArgsConstructor
@RestController
//@CrossOrigin(origins = CORS_ORIGIN)
@RequestMapping("/chat")
public class ChatController {
    private final ChatService chatService;
    @PostMapping("/new")
    public ResponseEntity<String> createRoom(@RequestBody ChatRoomReqDto chatRoomDto) { // RequestBody 요청부분
        log.warn("chatRoomDto : {}", chatRoomDto);
        ChatRoomResDto room = chatService.createRoom(chatRoomDto.getName());    // ResponseBody 반응부분
        System.out.println(room.getRoomId());
        return new ResponseEntity<>(room.getRoomId(), HttpStatus.OK);
    }
    @GetMapping("/list")
    public List<ChatRoomResDto> findAllRoom() {
        return chatService.findAllRoom();
    }

    @GetMapping("/chatroom/{roomId}")
    public ResponseEntity<ChatRoomResDto> chatRoomInfo(@PathVariable String roomId) {
        ChatRoomResDto room = chatService.findRoomById(roomId);
        return ResponseEntity.ok(room);
    }

    // 방 정보 가져오기
    @GetMapping("/room/{roomId}")
    public ChatRoomResDto findRoomById(@PathVariable String roomId) {
        return chatService.findRoomById(roomId);
    }
}