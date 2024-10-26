package com.woo.codeapiserver.code.impl;

import com.woo.codeapiserver.code.Runner;
import com.woo.codeapiserver.dto.code.CodeRespDto;

import java.io.BufferedReader;
import java.io.File;
import java.io.InputStreamReader;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.UUID;
import java.util.concurrent.TimeUnit;

public class JavaRunnerImpl implements Runner {
    private Process process; // 프로세스를 멤버 변수로 관리

    @Override
    public CodeRespDto runCode(String code, String input) throws Exception {
        String uuid = "C" + UUID.randomUUID().toString().replace("-", "");
        String javaFileName = uuid + ".java";
        String className = uuid;

        // 코드에 필요한 import 추가
        String modifiedCode = "import java.util.*;\nimport java.io.*;\n\n" +
                code.replaceFirst("public class [a-zA-Z0-9_]+", "public class " + className);

        Files.write(Paths.get(javaFileName), modifiedCode.getBytes());

        try {
            // 컴파일 단계
            ProcessBuilder compileProcessBuilder = new ProcessBuilder("javac", javaFileName);
            process = compileProcessBuilder.start();

            BufferedReader compileErrorReader = new BufferedReader(new InputStreamReader(process.getErrorStream()));
            StringBuilder compileErrorOutput = new StringBuilder();
            String line;
            while ((line = compileErrorReader.readLine()) != null) {
                compileErrorOutput.append(line.replace(javaFileName + ":", "Main.java:")).append("\n");
            }

            int compileExitCode = process.waitFor();
            if (compileExitCode != 0) {
                return CodeRespDto.builder()
                        .success(false)
                        .output(compileErrorOutput.toString())
                        .build();
            }

            // 실행 단계
            ProcessBuilder runProcessBuilder = new ProcessBuilder("java", className);
            process = runProcessBuilder.start();

            if (input != null && !input.isEmpty()) {
                process.getOutputStream().write(input.getBytes());
                process.getOutputStream().flush();
                process.getOutputStream().close();
            }

            BufferedReader runReader = new BufferedReader(new InputStreamReader(process.getInputStream()));
            StringBuilder runOutput = new StringBuilder();
            while ((line = runReader.readLine()) != null) {
                runOutput.append(line).append("\n");
            }

            BufferedReader runErrorReader = new BufferedReader(new InputStreamReader(process.getErrorStream()));
            StringBuilder runErrorOutput = new StringBuilder();
            while ((line = runErrorReader.readLine()) != null) {
                runErrorOutput.append(line).append("\n");
            }

            int runExitCode = process.waitFor();
            if (runExitCode != 0) {
                return CodeRespDto.builder()
                        .success(false)
                        .output(runErrorOutput.toString())
                        .build();
            }

            return CodeRespDto.builder()
                    .success(true)
                    .output(runOutput.toString())
                    .build();

        } finally {
            // 실행이 완료되면 Java 파일 및 클래스 파일 삭제
            Files.deleteIfExists(Paths.get(javaFileName));
            Files.deleteIfExists(Paths.get(className + ".class"));
        }
    }

    @Override
    public void stopCode() {
        if (process != null && process.isAlive()) {
            process.destroy(); // 프로세스 종료 시도
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
