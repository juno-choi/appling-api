package com.juno.appling.member.controller.request.kakao;

import com.juno.appling.member.controller.response.kakao.KakaoProfile;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class KakaoAccountRequest {

    public boolean has_email;
    public String email;
    public KakaoProfile profile;
}
