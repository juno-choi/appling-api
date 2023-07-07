package com.juno.appling.domain.common.vo;

import com.juno.appling.config.base.BaseVo;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class UploadVo extends BaseVo {
    private String imageUrl;
}
