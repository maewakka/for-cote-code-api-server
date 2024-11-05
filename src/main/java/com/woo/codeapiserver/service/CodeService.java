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

import java.util.Optional;
import java.util.concurrent.*;

@Service
@Slf4j
@RequiredArgsConstructor
public class CodeService {

    private final CodeRepository codeRepository;

    @Transactional
    public CodeRespDto runCode(CodeReqDto codeReqDto) {
        final Optional<Runner> runner;

        log.info("REQ : {}", codeReqDto);

        switch (codeReqDto.getLanguage()) {
            case JAVA -> runner = Optional.of(new JavaRunnerImpl());
            case PYTHON -> runner = Optional.of(new PythonRunnerImpl());
            default -> throw new IllegalArgumentException("지원하지 않는 언어입니다.");
        }

        ExecutorService executorService = Executors.newSingleThreadExecutor();
        Future<CodeRespDto> future = executorService.submit(() -> {
            if (codeReqDto.getId() != null) {
                Code code = codeRepository.findCodeByUserIdAndProblemIdAndLanguage(
                        codeReqDto.getId(),
                        codeReqDto.getProblemId(),
                        codeReqDto.getLanguage()).orElse(null);
                if (code == null) {
                    codeRepository.save(Code.builder()
                            .code(codeReqDto.getCode())
                            .userId(codeReqDto.getId())
                            .language(codeReqDto.getLanguage())
                            .problemId(codeReqDto.getProblemId())
                            .build());
                } else {
                    code.updateCode(codeReqDto.getCode());
                    codeRepository.save(code);
                }
            }

            try {
                return runner.get().runCode(codeReqDto.getCode(), codeReqDto.getInput());
            } catch (Exception e) {
                log.error("Runner execution failed", e);
                return CodeRespDto.builder()
                        .success(false)
                        .output("Execution failed: " + e.getMessage())
                        .build();
            }
        });

        try {
            // Future에서 30초로 제한 시간 설정
            return future.get(10, TimeUnit.SECONDS);
        } catch (TimeoutException e) {
            log.error("코드 실행 타임아웃", e);
            return CodeRespDto.builder()
                    .success(false)
                    .output("Execution timed out.")
                    .build();
        } catch (Exception e) {
            log.error("Unexpected error during code execution", e);
            return null;
        } finally {
            runner.get().stopCode();
        }
    }



    @Transactional
    public void saveCode(CodeReqDto codeReqDto){
        if(codeReqDto.getId() != null) {
            Code code = codeRepository.findCodeByUserIdAndProblemIdAndLanguage(codeReqDto.getId(), codeReqDto.getProblemId(), codeReqDto.getLanguage()).orElse(null);
            if(code == null) codeRepository.save(Code.builder().code(codeReqDto.getCode()).userId(codeReqDto.getId()).language(codeReqDto.getLanguage()).problemId(codeReqDto.getProblemId()).build());
            else {
                code.updateCode(codeReqDto.getCode());
            }
        }
    }

    @Transactional(readOnly = true)
    public String getCode(String id, Long problemId, Language language) {
        Code code = codeRepository.findCodeByUserIdAndProblemIdAndLanguage(id, problemId, language).orElse(null);

        return code == null ? null : code.getCode();
    }

}
