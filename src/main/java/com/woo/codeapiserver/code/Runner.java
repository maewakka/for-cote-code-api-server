package com.woo.codeapiserver.code;

import com.woo.codeapiserver.dto.code.CodeRespDto;

public interface Runner {

    CodeRespDto runCode(String code, String input) throws Exception;

}
