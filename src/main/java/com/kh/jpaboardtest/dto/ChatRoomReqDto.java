package com.kh.jpaboardtest.dto;

import lombok.Getter;
import lombok.Setter;

@Getter @Setter
public class ChatRoomReqDto {
    // 채팅방 생성 요청 시 전달되는 데이터
    // 채팅방에서 누가 보냈는지 볼 때 이메일과 이름 뜨는 것
    private String email;
    private String name;
}
