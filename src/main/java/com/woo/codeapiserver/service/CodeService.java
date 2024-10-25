package com.woo.codeapiserver.service;

import com.woo.codeapiserver.code.Runner;
import com.woo.codeapiserver.code.impl.JavaRunnerImpl;
import com.woo.codeapiserver.code.impl.PythonRunnerImpl;
import com.woo.codeapiserver.dto.code.CodeReqDto;
import com.woo.codeapiserver.dto.code.CodeRespDto;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Service
@Slf4j
@RequiredArgsConstructor
public class CodeService {

    public CodeRespDto runCode(CodeReqDto codeReqDto) {
        Runner runner = null;

        switch (codeReqDto.getLanguage()) {
            case JAVA -> runner = new JavaRunnerImpl();
            case PYTHON -> runner = new PythonRunnerImpl();
        }

        try {
            return runner.runCode(codeReqDto.getCode(), codeReqDto.getInput());
        } catch (Exception e) {
            log.error(e.getMessage(), e);
            return null;
        }
    }

    public void saveCode(CodeReqDto codeReqDto){
        if(codeReqDto.getEmail() != null) {

        }
    }

}
