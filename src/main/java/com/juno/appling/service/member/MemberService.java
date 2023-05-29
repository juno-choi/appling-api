package com.juno.appling.service.member;

import com.juno.appling.common.security.TokenProvider;
import com.juno.appling.domain.dto.member.JoinDto;
import com.juno.appling.domain.dto.member.LoginDto;
import com.juno.appling.domain.entity.member.Member;
import com.juno.appling.domain.vo.member.JoinVo;
import com.juno.appling.domain.vo.member.LoginVo;
import com.juno.appling.domain.vo.member.MemberVo;
import com.juno.appling.repository.member.MemberRepository;
import io.jsonwebtoken.Claims;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.ValueOperations;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import java.util.Date;
import java.util.Optional;

import static org.springframework.http.HttpHeaders.AUTHORIZATION;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class MemberService {
    private final MemberRepository memberRepository;
    private final BCryptPasswordEncoder passwordEncoder;
    private final AuthenticationManagerBuilder authenticationManagerBuilder;
    private final TokenProvider tokenProvider;
    private final RedisTemplate<String, Object> redisTemplate;

    private static final String AUTHORITIES_KEY = "auth";
    private static final String TYPE = "Bearer ";
    private static final long ACCESS_TOKEN_EXPIRE_TIME = 1000 * 60 * 30;            // 30분

    @Transactional
    public JoinVo join(JoinDto joinDto){
        joinDto.passwordEncoder(passwordEncoder);
        Optional<Member> findMember = memberRepository.findByEmail(joinDto.getEmail());
        if(findMember.isPresent()){
            throw new IllegalArgumentException("이미 존재하는 회원입니다.");
        }

        Member saveMember = memberRepository.save(Member.createMember(joinDto));
        return JoinVo.builder()
                .email(saveMember.getEmail())
                .name(saveMember.getName())
                .nickname(saveMember.getNickname())
                .build();
    }

    public LoginVo login(LoginDto loginDto) {
        // id, pw 기반으로 UsernamePasswordAuthenticationToken 객체 생
        UsernamePasswordAuthenticationToken authenticationToken = loginDto.toAuthentication();

        // security에 구현한 AuthService가 싫행됨
        Authentication authenticate = authenticationManagerBuilder.getObject().authenticate(authenticationToken);

        LoginVo loginVo = tokenProvider.generateTokenDto(authenticate);

        // token redis 저장
        String accessToken = loginVo.getAccessToken();
        String refreshToken = loginVo.getRefreshToken();
        Claims claims = tokenProvider.parseClaims(refreshToken);
        long refreshTokenExpired = Long.parseLong(claims.get("exp").toString());

        ValueOperations<String, Object> ops = redisTemplate.opsForValue();
        ops.set(refreshToken, accessToken);
        redisTemplate.expireAt(refreshToken, new Date(refreshTokenExpired*1000L));

        return loginVo;
    }

    public LoginVo refresh(String refreshToken) {
        if(!tokenProvider.validateToken(refreshToken)){
            throw new IllegalArgumentException("유효하지 않은 토큰입니다.");
        }

        ValueOperations<String, Object> ops = redisTemplate.opsForValue();
        String originAccessToken = ops.get(refreshToken).toString();
        Claims claims = tokenProvider.parseClaims(originAccessToken);

        String sub = claims.get("sub").toString();
        long now = (new Date()).getTime();
        Date accessTokenExpired = new Date(now + ACCESS_TOKEN_EXPIRE_TIME);
        String accessToken = tokenProvider.createAccessToken(sub, claims.get(AUTHORITIES_KEY).toString(), accessTokenExpired);

        Authentication authentication = tokenProvider.getAuthentication(accessToken);
        SecurityContextHolder.getContext().setAuthentication(authentication);

        return LoginVo.builder()
                .type(TYPE)
                .accessToken(accessToken)
                .accessTokenExpired(accessTokenExpired.getTime())
                .refreshToken(refreshToken)
                .build();
    }

    public MemberVo member(HttpServletRequest request) {
        String token = resolveToken(request);
        Long memberId = tokenProvider.getMemberId(token);

        Member findMember = memberRepository.findById(memberId).orElseThrow(() ->
                new IllegalArgumentException("존재하지 않는 회원입니다.")
        );

        return MemberVo.builder()
                .memberId(findMember.getId())
                .email(findMember.getEmail())
                .role(findMember.getRole())
                .createdAt(findMember.getCreatedAt())
                .modifiedAt(findMember.getModifiedAt())
                .name(findMember.getName())
                .nickname(findMember.getNickname())
                .snsType(findMember.getSnsType())
                .build();
    }
    private String resolveToken(HttpServletRequest request) {
        String bearerToken = request.getHeader(AUTHORIZATION);
        if (StringUtils.hasText(bearerToken) && bearerToken.startsWith(TYPE)) {
            return bearerToken.substring(TYPE.length());
        }
        return null;
    }
}
