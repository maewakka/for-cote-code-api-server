package com.woo.codeapiserver.service;

import com.woo.codeapiserver.code.Runner;
import com.woo.codeapiserver.code.impl.JavaRunnerImpl;
import com.woo.codeapiserver.code.impl.PythonRunnerImpl;
import com.woo.codeapiserver.dto.code.CodeReqDto;
import com.woo.codeapiserver.dto.code.CodeRespDto;
import com.woo.codeapiserver.dto.enums.Language;
import com.woo.codeapiserver.entity.Code;
import com.woo.codeapiserver.repository.CodeRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Slf4j
@RequiredArgsConstructor
public class CodeService {

    private final CodeRepository codeRepository;

    @Transactional
    public CodeRespDto runCode(CodeReqDto codeReqDto) {
        Runner runner = null;

        switch (codeReqDto.getLanguage()) {
            case JAVA -> runner = new JavaRunnerImpl();
            case PYTHON -> runner = new PythonRunnerImpl();
        }

        try {
            if(codeReqDto.getEmail() != null) {
                Code code = codeRepository.findCodeByEmailAndProblemIdAndLanguage(codeReqDto.getEmail(), codeReqDto.getProblemId(), codeReqDto.getLanguage()).orElse(null);
                if(code == null) codeRepository.save(Code.builder().code(codeReqDto.getCode()).email(codeReqDto.getEmail()).language(codeReqDto.getLanguage()).problemId(codeReqDto.getProblemId()).build());
                else {
                    code.updateCode(codeReqDto.getCode());
                }
            }
            return runner.runCode(codeReqDto.getCode(), codeReqDto.getInput());
        } catch (Exception e) {
            log.error(e.getMessage(), e);
            return null;
        }
    }

    @Transactional
    public void saveCode(CodeReqDto codeReqDto){
        if(codeReqDto.getEmail() != null) {
            Code code = codeRepository.findCodeByEmailAndProblemIdAndLanguage(codeReqDto.getEmail(), codeReqDto.getProblemId(), codeReqDto.getLanguage()).orElse(null);
            if(code == null) codeRepository.save(Code.builder().code(codeReqDto.getCode()).email(codeReqDto.getEmail()).language(codeReqDto.getLanguage()).problemId(codeReqDto.getProblemId()).build());
            else {
                code.updateCode(codeReqDto.getCode());
            }
        }
    }

    @Transactional(readOnly = true)
    public String getCode(String email, Long problemId, Language language) {
        Code code = codeRepository.findCodeByEmailAndProblemIdAndLanguage(email, problemId, language).orElse(null);

        return code == null ? null : code.getCode();
    }

}
