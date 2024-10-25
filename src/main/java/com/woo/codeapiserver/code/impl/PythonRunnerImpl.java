package com.woo.codeapiserver.code.impl;

import com.woo.codeapiserver.code.Runner;
import com.woo.codeapiserver.dto.code.CodeRespDto;

import java.io.BufferedReader;
import java.io.InputStreamReader;

public class PythonRunnerImpl implements Runner {

    @Override
    public CodeRespDto runCode(String code, String input) throws Exception{
        ProcessBuilder pb = new ProcessBuilder("python3", "-c", code);
        Process process = pb.start();
        process.getOutputStream().write(input.getBytes());
        process.getOutputStream().flush();
        process.getOutputStream().close();

        BufferedReader reader = new BufferedReader(new InputStreamReader(process.getInputStream()));
        StringBuilder result = new StringBuilder();
        String line;
        while ((line = reader.readLine()) != null) {
            result.append(line).append("\n");
        }

        // Python 실행 종료 코드 확인
        int exitCode = process.waitFor();

        if (exitCode == 0) {
            return CodeRespDto.builder()
                    .success(true)
                    .output(result.toString())
                    .build();
        } else {
            BufferedReader errorReader = new BufferedReader(new InputStreamReader(process.getErrorStream()));
            StringBuilder errorOutput = new StringBuilder();
            while ((line = errorReader.readLine()) != null) {
                errorOutput.append(line).append("\n");
            }
            return CodeRespDto.builder()
                    .success(false)
                    .output(errorOutput.toString())
                    .build();
        }
    }
}
