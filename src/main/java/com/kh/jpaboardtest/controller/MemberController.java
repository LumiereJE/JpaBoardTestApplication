package com.kh.jpaboardtest.controller;


//import com.kh.jpaboardtest.dto.MemberDto;
import com.kh.jpaboardtest.dto.MemberResDto;
import com.kh.jpaboardtest.dto.MemberReqDto;
import com.kh.jpaboardtest.security.SecurityUtil;
import com.kh.jpaboardtest.service.MemberService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

//import static com.kh.jpaboardtest.utils.Common.CORS_ORIGIN;


@Slf4j
//@CrossOrigin(origins = CORS_ORIGIN)
@RestController
@RequestMapping("/users")
@RequiredArgsConstructor
public class MemberController {
    private final MemberService memberService;

    // 회원 전체 조회
    @GetMapping("/list")
    public ResponseEntity<List<MemberResDto>> memberList() {
        List<MemberResDto> list = memberService.getMemberList();
        return ResponseEntity.ok(list);
    }

    // 총 페이지 수
    @GetMapping("/list/count")
    public ResponseEntity<Integer> memberCount(@RequestParam(defaultValue = "20") int page,
                                               @RequestParam(defaultValue = "0") int size) {
        PageRequest pageRequest = PageRequest.of(page, size);
        int pageCnt = memberService.getMemberPage(pageRequest);
        return ResponseEntity.ok(pageCnt);
    }

    // 회원 조회 페이지네이션
    @GetMapping("/list/page")
    public ResponseEntity<List<MemberResDto>> memberList(@RequestParam(defaultValue = "0") int page,
                                                      @RequestParam(defaultValue = "20") int size) {
        List<MemberResDto> list = memberService.getMemberList(page, size);
        return ResponseEntity.ok(list);
    }

    // 회원 상세 조회
    @GetMapping("/detail")
    public ResponseEntity<MemberResDto> memberDetail() {
        Long id = SecurityUtil.getCurrentMemberId();
        MemberResDto memberDto = memberService.getMemberDetail(id);
        return ResponseEntity.ok(memberDto);
    }

    // 회원 수정
    @PutMapping("/modify")
    public ResponseEntity<Boolean> memberModify(@RequestBody MemberReqDto memberDto) {
        log.info("memberDto: {}", memberDto.getEmail());
        boolean isTrue = memberService.modifyMember(memberDto);
        return ResponseEntity.ok(isTrue);
    }

    // AuthService로 옮기면서 삭제됨
//    // 회원 등록
//    @PostMapping("/new")
//    public ResponseEntity<Boolean> memberRegister(@RequestBody MemberDto memberDto) {
//        boolean isTrue = memberService.saveMember(memberDto);
//        return ResponseEntity.ok(isTrue);
//    }
//    // AuthService로 옮기면서 삭제됨
//    // 로그인
//    @PostMapping("/login")
//    public ResponseEntity<Boolean> memberLogin(@RequestBody MemberDto memberDto) {
//        boolean isTrue = memberService.login(memberDto.getEmail(), memberDto.getPwd());
//        return ResponseEntity.ok(isTrue);
//    }

//    // 회원 존재 여부 확인
//    @GetMapping("/check")
//    public ResponseEntity<Boolean> isMember(@RequestParam String email) {
//        log.info("email: {}", email);
//        boolean isReg = memberService.isMember(email);
//        return ResponseEntity.ok(!isReg);
//    }

    // 회원 삭제
    @DeleteMapping("/del/{email}")
    public ResponseEntity<Boolean> memberDelete(@PathVariable String email) {
        boolean isTrue = memberService.deleteMember(email);
        return ResponseEntity.ok(isTrue);
    }
}
