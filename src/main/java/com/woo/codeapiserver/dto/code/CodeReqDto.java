package com.woo.codeapiserver.dto.code;

import com.woo.codeapiserver.dto.enums.Language;
import lombok.Data;

@Data
public class CodeReqDto {

    private Language language;
    private String email;
    private String code;
    private String input;

}
