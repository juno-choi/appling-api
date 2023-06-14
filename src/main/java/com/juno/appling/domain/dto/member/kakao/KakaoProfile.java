package com.juno.appling.domain.dto.member.kakao;

import lombok.*;

@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class KakaoProfile {
    public String nickname;
    public String thumbnail_image_url;
    public String profile_image_url;
}
