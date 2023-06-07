package com.juno.appling.domain.vo.common;

import com.juno.appling.domain.vo.BaseVo;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class UploadVo extends BaseVo {
    private String imageUrl;
}
