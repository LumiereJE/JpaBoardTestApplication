package com.kh.jpaboardtest.service;


import com.kh.jpaboardtest.dto.MemberReqDto;
import com.kh.jpaboardtest.dto.MemberResDto;
import com.kh.jpaboardtest.dto.TokenDto;
import com.kh.jpaboardtest.entity.Member;
import com.kh.jpaboardtest.jwt.TokenProvider;
import com.kh.jpaboardtest.repository.MemberRepository;


import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import javax.transaction.Transactional;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional
public class AuthService {
    private final AuthenticationManagerBuilder managerBuilder; // 인증을 담당하는 클래스
    private final MemberRepository memberRepository;
    private final PasswordEncoder passwordEncoder;
    private final TokenProvider tokenProvider;

    // Member Controller 와 MemberService 둘 다 삭제후 여기서 처리함 -> 구조가 조금 바뀜
    // 회원가입 여부 확인 / 회원가입 / 로그인
    // 토큰 발급 전에 움직여야 할 애들임 -> 토큰 없어도 할 수 있는 애들

    public MemberResDto signup(MemberReqDto requestDto) {
        if (memberRepository.existsByEmail(requestDto.getEmail())) {
            throw new RuntimeException("이미 가입되어 있는 유저입니다");
        }
        Member member = requestDto.toEntity(passwordEncoder);
        return MemberResDto.of(memberRepository.save(member));
    }

    public TokenDto login(MemberReqDto requestDto) {
//        try {
        UsernamePasswordAuthenticationToken authenticationToken = requestDto.toAuthentication();
        log.info("authenticationToken: {}", authenticationToken);

        Authentication authentication = managerBuilder.getObject().authenticate(authenticationToken);
        log.info("authentication: {}", authentication);

        return tokenProvider.generateTokenDto(authentication);
//        } catch (Exception e) {
//            log.error("Login error: ", e);
//            throw e;
//        }
    }

    public TokenDto refreshAccessToken(String refreshToken) {
        try {
            if (tokenProvider.validateToken(refreshToken)) {
                return tokenProvider.generateTokenDto(tokenProvider.getAuthentication(refreshToken));
            }
        }catch (RuntimeException e) {
            log.error("토큰 유효성 검증 중 예외 발생: {}", e.getMessage());
        }
        return null;
    }


}