package com.limbrescue.limbrescueangularappbackend.ml;

import java.io.*;
import java.util.List;
import java.util.stream.Collectors;

public class RandomForest {
    public void run() {
        ProcessBuilder processBuilder = new ProcessBuilder("python", resolvePythonScriptPath("train_net.py"));
        processBuilder.redirectErrorStream(true);
        try {
            Process process = processBuilder.start();
            List<String> results = readProcessOutput(process.getInputStream());
        } catch (Exception e) {
            e.printStackTrace();
        }
//        System.out.println(results);
    }
    private List<String> readProcessOutput(InputStream inputStream) throws IOException {
        try (BufferedReader output = new BufferedReader(new InputStreamReader(inputStream))) {
            return output.lines()
                    .collect(Collectors.toList());
        }
    }
    private String resolvePythonScriptPath(String filename) {
        File file = new File("src/main/resources/rf/" + filename);
        return file.getAbsolutePath();
    }
}
