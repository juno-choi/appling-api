package com.juno.appling.domain.vo.member;

import com.juno.appling.domain.enums.member.RecipientInfoStatus;
import com.juno.appling.domain.vo.BaseVo;
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
