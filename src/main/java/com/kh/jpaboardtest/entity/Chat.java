package com.kh.jpaboardtest.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.kh.jpaboardtest.dto.ChatMessageDto;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

import javax.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "chat")
@Getter @Setter @ToString
public class Chat {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "chat_id")
    private String chatId;
    @Column(name = "sender")
    private String sender;
    @Column(name = "setSenderName")
    private String senderName;
    @Column(name = "message")
    private String message;
    @Column(name = "sent_at")
    private LocalDateTime sentAt;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "member_id")
    @JsonIgnore
    private Member member;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "room_id")
    @JsonIgnore
    private ChatRoom chatRoom;

}
