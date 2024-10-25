package com.woo.codeapiserver.code.impl;

import com.woo.codeapiserver.code.Runner;
import com.woo.codeapiserver.dto.code.CodeRespDto;

import java.io.BufferedReader;
import java.io.File;
import java.io.InputStreamReader;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.UUID;

public class JavaRunnerImpl implements Runner {

    @Override
    public CodeRespDto runCode(String code, String input) throws Exception {
        String uuid = "C" + UUID.randomUUID().toString().replace("-", "");
        String javaFileName = uuid + ".java";
        String className = uuid;

        String modifiedCode = code.replaceFirst("public class [a-zA-Z0-9_]+", "public class " + className);
        Files.write(Paths.get(javaFileName), modifiedCode.getBytes());

        try {
            ProcessBuilder compileProcessBuilder = new ProcessBuilder("javac", javaFileName);
            Process compileProcess = compileProcessBuilder.start();

            BufferedReader compileErrorReader = new BufferedReader(new InputStreamReader(compileProcess.getErrorStream()));
            StringBuilder compileErrorOutput = new StringBuilder();
            String line;
            while ((line = compileErrorReader.readLine()) != null) {
                compileErrorOutput.append(line.replace(javaFileName + ":", "Main.java:")).append("\n");
            }

            int compileExitCode = compileProcess.waitFor();
            if (compileExitCode != 0) {
                return CodeRespDto.builder()
                        .success(false)
                        .output(compileErrorOutput.toString())
                        .build();
            }

            ProcessBuilder runProcessBuilder = new ProcessBuilder("java", className);
            Process runProcess = runProcessBuilder.start();

            if (input != null && !input.isEmpty()) {
                runProcess.getOutputStream().write(input.getBytes());
                runProcess.getOutputStream().flush();
                runProcess.getOutputStream().close();
            }

            BufferedReader runReader = new BufferedReader(new InputStreamReader(runProcess.getInputStream()));
            StringBuilder runOutput = new StringBuilder();
            while ((line = runReader.readLine()) != null) {
                runOutput.append(line).append("\n");
            }

            BufferedReader runErrorReader = new BufferedReader(new InputStreamReader(runProcess.getErrorStream()));
            StringBuilder runErrorOutput = new StringBuilder();
            while ((line = runErrorReader.readLine()) != null) {
                runErrorOutput.append(line).append("\n");
            }

            int runExitCode = runProcess.waitFor();
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
            Files.deleteIfExists(Paths.get(javaFileName));
            Files.deleteIfExists(Paths.get(className + ".class"));
        }
    }
}
