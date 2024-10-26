package com.woo.codeapiserver.code.impl;

import com.woo.codeapiserver.code.Runner;
import com.woo.codeapiserver.dto.code.CodeRespDto;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.util.concurrent.TimeUnit;

public class PythonRunnerImpl implements Runner {
    private Process process; // 프로세스 멤버 변수 추가

    @Override
    public CodeRespDto runCode(String code, String input) throws Exception {
        ProcessBuilder pb = new ProcessBuilder("python3", "-c", code);
        process = pb.start(); // 프로세스를 멤버 변수에 저장
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

    @Override
    public void stopCode() {
        if (process != null && process.isAlive()) {
            process.destroy(); // 프로세스가 실행 중인 경우 종료
            try {
                if (!process.waitFor(5, TimeUnit.SECONDS)) { // 5초 내에 종료되지 않으면 강제 종료
                    process.destroyForcibly();
                }
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
                process.destroyForcibly();
            }
        }
    }
}