package com.juno.appling.global.base;

import lombok.*;

@Builder
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class Api<T> {

    @NonNull
    private String code;
    @NonNull
    private String message;
    @NonNull
    private T data;
}
