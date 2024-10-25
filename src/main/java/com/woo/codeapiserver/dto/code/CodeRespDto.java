package com.woo.codeapiserver.dto.code;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class CodeRespDto {

    private Boolean success;
    private String output;

}
