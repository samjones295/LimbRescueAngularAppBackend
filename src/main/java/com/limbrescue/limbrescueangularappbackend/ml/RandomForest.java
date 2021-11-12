package com.limbrescue.limbrescueangularappbackend.ml;

import java.io.*;
import java.util.List;
import java.util.stream.Collectors;

public class RandomForest {
    public void run() throws Exception{
        ProcessBuilder processBuilder = new ProcessBuilder("python", resolvePythonScriptPath("rf.py"));
        processBuilder.redirectErrorStream(true);

        Process process = processBuilder.start();
        List<String> results = readProcessOutput(process.getInputStream());
        System.out.println(results);
    }
    private List<String> readProcessOutput(InputStream inputStream) throws IOException {
        try (BufferedReader output = new BufferedReader(new InputStreamReader(inputStream))) {
            return output.lines()
                    .collect(Collectors.toList());
        }
    }
    private String resolvePythonScriptPath(String filename) {
        File file = new File("src/main/resources/LimbRescueML/limbresml/modeling/" + filename);
        return file.getAbsolutePath();
    }
}
