package com.juno.appling.member.dto.response.kakao;

import com.juno.appling.member.dto.request.kakao.KakaoAccountRequest;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class KakaoMemberResponse {

    public Long id;
    public KakaoAccountRequest kakao_account;
}
