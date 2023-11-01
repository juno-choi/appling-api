package com.juno.appling.member.service;

import com.juno.appling.member.controller.request.JoinRequest;
import com.juno.appling.member.controller.request.LoginRequest;
import com.juno.appling.member.controller.response.JoinResponse;
import com.juno.appling.member.controller.response.LoginResponse;

public interface MemberAuthService {

    JoinResponse join(JoinRequest joinRequest);
    LoginResponse login(LoginRequest loginRequest);
    LoginResponse refresh(String refreshToken);
    LoginResponse authKakao(String code);
    LoginResponse loginKakao(String accessToken);
}
