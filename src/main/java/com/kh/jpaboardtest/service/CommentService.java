package com.kh.jpaboardtest.service;

import com.kh.jpaboardtest.dto.BoardDto;
import com.kh.jpaboardtest.dto.CommentDto;
import com.kh.jpaboardtest.entity.Board;
import com.kh.jpaboardtest.entity.Comment;
import com.kh.jpaboardtest.entity.Member;
import com.kh.jpaboardtest.repository.BoardRepository;
import com.kh.jpaboardtest.repository.CommentRepository;
import com.kh.jpaboardtest.repository.MemberRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

import static com.kh.jpaboardtest.security.SecurityUtil.getCurrentMemberId;

@Service
@RequiredArgsConstructor
public class CommentService {
    private final CommentRepository commentRepository;
    private final BoardRepository boardRepository;
    private final MemberRepository memberRepository;
    // 댓글 등록
    public boolean commentRegister(CommentDto commentDto) {
        try {
            Comment comment = new Comment();
            Board board = boardRepository.findById(commentDto.getBoardId()).orElseThrow(
                    () -> new RuntimeException("해당 게시글이 존재하지 않습니다.")
            );
//            Member member = memberRepository.findByEmail(commentDto.getEmail()).orElseThrow(
            Long memberId = getCurrentMemberId();
            Member member = memberRepository.findById(memberId).orElseThrow(
                    () -> new RuntimeException("해당 회원이 존재하지 않습니다.")
            );
            comment.setContent(commentDto.getContent());
            comment.setMember(member);
            comment.setBoard(board);
            commentRepository.save(comment);
            return true;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }
    // 댓글 수정
    public boolean commentModify(CommentDto commentDto) {
        try {
            Comment comment = commentRepository.findById(commentDto.getCommentId()).orElseThrow(
                    () -> new RuntimeException("해당 댓글이 존재하지 않습니다.")
            );
            comment.setContent(commentDto.getContent());
            commentRepository.save(comment);
            return true;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }
    // 댓글 삭제
    public boolean commentDelete(Long commentId) {
        try {
            Comment comment = commentRepository.findById(commentId).orElseThrow(
                    () -> new RuntimeException("해당 댓글이 존재하지 않습니다.")
            );
            commentRepository.delete(comment);
            return true;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }
    // 댓글 조회
    public List<CommentDto> getCommentList(Long boardId) {
        try {
            Board board = boardRepository.findById(boardId).orElseThrow(
                    () -> new RuntimeException("해당 게시글이 존재하지 않습니다.")
            );
            List<Comment> comments = commentRepository.findByBoard(board);
            List<CommentDto> commentDtos = new ArrayList<>();
            for (Comment comment : comments) {
                commentDtos.add(converEntityToDto(comment));
            }
            return commentDtos;
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }
    // 댓글 상세 조회
    public CommentDto getCommentDetail(Long id) {
        Comment comment = commentRepository.findById(id).orElseThrow(
                () -> new RuntimeException("해당 게시글이 존재하지 않습니다.")
        );
        return converEntityToDto(comment);
    }
    // 댓글 검색
    public List<CommentDto> getCommentSearch(String keyword) {
        List<Comment> comments = commentRepository.findByContentContaining(keyword);
        List<CommentDto> commentDtos = new ArrayList<>();
        for (Comment comment : comments) {
            commentDtos.add(converEntityToDto(comment));
        }
        return commentDtos;
    }
//     댓글 목록 페이징
    public List<CommentDto> getCommentList(int page, int size) {
        Pageable pageable = PageRequest.of(page, size);
        List<Comment> comments = commentRepository.findAll(pageable).getContent();
        List<CommentDto> commentDtos = new ArrayList();
        for(Comment comment : comments) {
            commentDtos.add(converEntityToDto(comment));
        }
        return commentDtos;
    }
    // 댓글 엔티티를 DTO로 변환
    private CommentDto converEntityToDto(Comment comment) {
        CommentDto commentDto = new CommentDto();
        commentDto.setCommentId(comment.getCommentId());
        commentDto.setBoardId(comment.getCommentId());
        commentDto.setEmail(comment.getMember().getEmail());
        commentDto.setContent(comment.getContent());
        commentDto.setRegDate(comment.getRegDate());
        return commentDto;
    }

}
