package com.juno.appling.domain.member.vo;

import com.juno.appling.domain.member.enums.RecipientInfoStatus;
import com.juno.appling.config.base.BaseVo;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

import java.time.LocalDateTime;

@Getter
@Builder
@AllArgsConstructor
public class RecipientVo extends BaseVo {
    private Long id;
    private String name;
    private String address;
    private String tel;
    private RecipientInfoStatus status;
    private LocalDateTime createdAt;
    private LocalDateTime modifiedAt;
}
