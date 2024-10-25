package com.woo.codeapiserver.controller;

import com.woo.codeapiserver.dto.code.CodeReqDto;
import com.woo.codeapiserver.dto.code.CodeRespDto;
import com.woo.codeapiserver.service.CodeService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
@Slf4j
@RequiredArgsConstructor
public class CodeController {

    private final CodeService codeService;

    @PostMapping("/execute")
    public CodeRespDto execute(@RequestBody CodeReqDto codeReqDto) {
        return codeService.runCode(codeReqDto);
    }

}
