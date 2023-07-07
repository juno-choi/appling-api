package com.juno.appling.domain.member.vo;


import com.juno.appling.config.base.BaseVo;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

import java.util.List;

@Getter
@Builder
@AllArgsConstructor
public class RecipientListVo extends BaseVo {
    private List<RecipientVo> list;
}
