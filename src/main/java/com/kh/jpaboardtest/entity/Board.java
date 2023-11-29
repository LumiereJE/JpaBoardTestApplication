package com.kh.jpaboardtest.entity;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

import javax.persistence.*;
import java.time.LocalDateTime;
import java.util.List;

@Entity
@Table(name = "board")
@Getter @Setter @ToString
@NoArgsConstructor
public class Board {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long boardId;
    private String title;
    private String content;
    private String imgPath;
    private LocalDateTime regDate;

    @PrePersist
    public void prePersist() {
        regDate = LocalDateTime.now();
    }

    // 연관 관계
    @ManyToOne(fetch = FetchType.LAZY)      // 지연 전략
    @JoinColumn(name = "member_id")         // 외래키
    private Member member;                  // 작성자


    // 카테고리 추가
    @ManyToOne(fetch = FetchType.LAZY)      // 지연 전략
    @JoinColumn(name = "category_id")       // 외래키
    private Category category;              // 카테고리


    // Board와 Comment는 1:N 관계, mappedBy는 연관관계의 주인이 아니다라는 의미(난 FK가 아니에요)
    @OneToMany(mappedBy = "board", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Comment> comments;         // 댓글 목록
}
