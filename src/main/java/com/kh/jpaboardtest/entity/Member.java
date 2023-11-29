package com.kh.jpaboardtest.entity;

import com.kh.jpaboardtest.constant.Authority;
import lombok.*;

import javax.persistence.*;
import java.time.LocalDateTime;
import java.util.List;

@Entity
@Table(name = "member")
@Getter @Setter @ToString
@NoArgsConstructor
public class Member {
    @Id
    @Column(name = "member_id")
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    private String name;
    private String password;

    @Column(unique = true)
    private String email;

    private String image;
    private LocalDateTime regDate;

    // JWT 추가하면서 생김
    @Enumerated(EnumType.STRING)
    private Authority authority;


    // JWT 추가하면서 삭제됨
//    @PrePersist
//    public void prePersist() {
//        regDate = LocalDateTime.now();
//    }

    // 연관 관계
    @OneToMany(mappedBy = "member", fetch = FetchType.LAZY)
    private List<Comment> comments;

    @OneToMany(mappedBy = "member", fetch = FetchType.LAZY)
    private List<Board> boards;

    // JWT 추가하면서 생김
    @Builder // 빌더 패턴 적용
    public Member(String name, String password, String email, String image, Authority authority) {
        this.name = name;
        this.password = password;
        this.email = email;
        this.image = image;
        this.authority = authority;
        this.regDate = LocalDateTime.now();
    }






}
