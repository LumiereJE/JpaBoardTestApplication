package com.kh.jpaboardtest.jwt;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.util.StringUtils;
import org.springframework.web.filter.OncePerRequestFilter;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

// 유효성 검사
// request 앞단에 붙이는 필터. http request에서 토큰을 받아와서 정상 토큰일 경우 security context에 저장함

@Slf4j
@RequiredArgsConstructor // final이 붙은 필드를 인자값으로 하는 생성자를 만들어줌
// @OncePerRequestFilter : http Request의 한번의 요청에 대해 한 번만 실행하는 Filter(인터페이스를 구현하기 때문에 요청받을 때 단 한번만 실행)
public class JwtFilter  extends OncePerRequestFilter {
    public static final String AUTHORIZATION_HEADER = "Authorization"; // 토큰을 요청 헤더의 Authorization 키에 담아서 전달
    public static final String BEARER_PREFIX = "Bearer "; // 토큰 앞에 붙는 문자열
    private final TokenProvider tokenProvider; // 토큰 생성, 토큰 검증을 수행하는 TokenProvider

    private String resolveToken(HttpServletRequest request) { // 토큰을 요청 헤더에서 꺼내오는 메서드
        String bearerToken = request.getHeader(AUTHORIZATION_HEADER); // 헤더에서 토큰 꺼내오기
        if (bearerToken != null && bearerToken.startsWith(BEARER_PREFIX)) { // 토큰이 존재하고, 토큰 앞에 붙는 문자열이 존재하면
            return bearerToken.substring(7); // 토큰 앞에 붙는 문자열을 제거하고 토큰 반환
        }
        return null;
    }

    // doFilterInternal : 실제 필터링 로직을 수행하는 곳
    // JWT 토큰의 인증 정보를 현재 쓰레드의 SecurityContext에 저장하는 역할 수행
    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
        String jwt = resolveToken(request);

        if (StringUtils.hasText(jwt) && tokenProvider.validateToken(jwt)) { // validateToken으로 토큰 유효성 검사
            Authentication authentication = tokenProvider.getAuthentication(jwt);
            SecurityContextHolder.getContext().setAuthentication(authentication);   // 정상 토큰이면 해당 토큰으로 Authentication을 가져와서 SecurityContext에 저장함
        }

        filterChain.doFilter(request, response);
    }
}