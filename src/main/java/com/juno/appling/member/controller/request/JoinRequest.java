package com.juno.appling.member.controller.request;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class JoinRequest {

    @NotNull(message = "email 비어있을 수 없습니다.")
    @Email(message = "email 형식을 맞춰주세요.", regexp = "^[A-Za-z0-9._%+-]+@[A-Za-z0-9.-]+.[A-Za-z]{2,6}$")
    private String email;
    @NotNull(message = "password 비어있을 수 없습니다.")
    private String password;
    @NotNull(message = "name 비어있을 수 없습니다.")
    private String name;
    @NotNull(message = "nickname 비어있을 수 없습니다.")
    private String nickname;
    private String birth;

    public void passwordEncoder(BCryptPasswordEncoder encoder) {
        this.password = encoder.encode(this.password);
    }
}
