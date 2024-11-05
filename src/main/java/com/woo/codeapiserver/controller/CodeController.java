package com.woo.codeapiserver.controller;

import com.woo.codeapiserver.dto.code.CodeReqDto;
import com.woo.codeapiserver.dto.code.CodeRespDto;
import com.woo.codeapiserver.dto.enums.Language;
import com.woo.codeapiserver.service.CodeService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@Slf4j
@RequiredArgsConstructor
public class CodeController {

    private final CodeService codeService;

    @PostMapping("/execute")
    public CodeRespDto execute(@RequestBody CodeReqDto codeReqDto) {
        return codeService.runCode(codeReqDto);
    }

    @PostMapping("/save")
    public ResponseEntity<String> saveCode(@RequestBody CodeReqDto codeReqDto) {
        codeService.saveCode(codeReqDto);

        return ResponseEntity.ok("코드를 저장하였습니다.");
    }

    @GetMapping("/code")
    public String getCode(@RequestParam("id") String id, @RequestParam("problemId") Long problemId, @RequestParam("language") Language language) {
        return codeService.getCode(id, problemId, language);
    }
}
