package com.juno.appling.service.member;

import com.juno.appling.common.security.TokenProvider;
import com.juno.appling.domain.dto.member.JoinDto;
import com.juno.appling.domain.dto.member.KakaoLoginResponseDto;
import com.juno.appling.domain.dto.member.LoginDto;
import com.juno.appling.domain.entity.member.Member;
import com.juno.appling.domain.enums.member.Role;
import com.juno.appling.domain.vo.member.JoinVo;
import com.juno.appling.domain.vo.member.LoginVo;
import com.juno.appling.repository.member.MemberRepository;
import io.jsonwebtoken.Claims;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.env.Environment;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.ValueOperations;
import org.springframework.http.HttpHeaders;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.reactive.function.BodyInserters;
import org.springframework.web.reactive.function.client.WebClient;

import java.util.*;

@Service
@RequiredArgsConstructor
@Slf4j
@Transactional(readOnly = true)
public class MemberAuthService {
    private final MemberRepository memberRepository;
    private final BCryptPasswordEncoder passwordEncoder;
    private final AuthenticationManagerBuilder authenticationManagerBuilder;
    private final TokenProvider tokenProvider;
    private final RedisTemplate<String, Object> redisTemplate;
    private final WebClient webClient;
    private final Environment env;

    private static final String AUTHORITIES_KEY = "auth";
    private static final String TYPE = "Bearer ";
    private static final long ACCESS_TOKEN_EXPIRE_TIME = 1000L * 60 * 30;            // 30분

    @Transactional
    public JoinVo join(JoinDto joinDto){
        joinDto.passwordEncoder(passwordEncoder);
        Optional<Member> findMember = memberRepository.findByEmail(joinDto.getEmail());
        if(findMember.isPresent()){
            throw new IllegalArgumentException("이미 존재하는 회원입니다.");
        }

        Member saveMember = memberRepository.save(Member.of(joinDto));
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
        String originAccessToken = Optional.ofNullable(ops.get(refreshToken)).orElse("").toString();
        Claims claims = tokenProvider.parseClaims(originAccessToken);

        String sub = claims.get("sub").toString();
        long now = (new Date()).getTime();
        Date accessTokenExpired = new Date(now + ACCESS_TOKEN_EXPIRE_TIME);

        Member member = memberRepository.findById(Long.parseLong(sub)).orElseThrow(() ->
                new IllegalArgumentException("유효하지 않은 회원입니다.")
        );

        Role role = Role.valueOf(member.getRole().role);
        String[] roleSplitList = role.roleList.split(",");
        List<String> trimRoleList = Arrays.stream(roleSplitList).map(r -> String.format("ROLE_%s", r.trim())).toList();
        String roleList = trimRoleList.toString().replace("[", "").replace("]", "").replace(" ", "");

        String accessToken = tokenProvider.createAccessToken(String.valueOf(member.getId()), roleList, accessTokenExpired);

        Authentication authentication = tokenProvider.getAuthentication(accessToken);
        SecurityContextHolder.getContext().setAuthentication(authentication);

        return LoginVo.builder()
                .type(TYPE)
                .accessToken(accessToken)
                .accessTokenExpired(accessTokenExpired.getTime())
                .refreshToken(refreshToken)
                .build();
    }

    @Transactional
    public LoginVo loginKakao(String code) {
        log.info("code ={}", code);

        MultiValueMap<String, String> map = new LinkedMultiValueMap<>();
        map.add("grant_type", "authorization_code");
        map.add("client_id", env.getProperty("kakao.client_id"));
        map.add("redirect_url", env.getProperty("kakao.redirect_url"));
        map.add("code", code);

        // TODO code를 통해 access token 발급 진행

        KakaoLoginResponseDto kakaoToken = WebClient.create("https://kauth.kakao.com/oauth/token").post()
                .header(HttpHeaders.CONTENT_TYPE, "application/x-www-form-urlencoded;charset=utf-8")
                .body(
                        BodyInserters.fromFormData(map)
                )
                .retrieve()
                .bodyToMono(KakaoLoginResponseDto.class)
                .block();

//        KakaoLoginResponseDto kakaoToken = webClient.post().uri("https://kauth.kakao.com/oauth/token")
//                .header(HttpHeaders.CONTENT_TYPE, "application/x-www-form-urlencoded;charset=utf-8")
//                .body(
//                        BodyInserters.fromFormData(map)
//                )
//                .retrieve()
//                .bodyToMono(KakaoLoginResponseDto.class)
//                .block();

        return LoginVo.builder()
                .type(TYPE)
                .accessToken(kakaoToken.access_token)
                .accessTokenExpired(kakaoToken.expires_in)
                .refreshToken(kakaoToken.refresh_token)
                .refreshTokenExpired(kakaoToken.refresh_token_expires_in)
                .build();
    }
}
