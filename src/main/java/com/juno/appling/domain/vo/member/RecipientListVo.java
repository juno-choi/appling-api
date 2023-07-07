package com.juno.appling.domain.vo.member;


import com.juno.appling.domain.vo.BaseVo;
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
