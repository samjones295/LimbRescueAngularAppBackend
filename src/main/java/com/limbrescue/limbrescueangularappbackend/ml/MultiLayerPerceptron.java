package com.limbrescue.limbrescueangularappbackend.ml;

import java.io.*;
import java.util.List;
import java.util.stream.Collectors;

public class MultiLayerPerceptron {
    public void run() {
        ProcessBuilder processBuilder = new ProcessBuilder("./mlp.bat");
        processBuilder.redirectErrorStream(true);
        try {
            Process process = processBuilder.start();
            List<String> results = readProcessOutput(process.getInputStream());
            for (String s : results) {
                System.out.println(s);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    private List<String> readProcessOutput(InputStream inputStream) throws IOException {
        try (BufferedReader output = new BufferedReader(new InputStreamReader(inputStream))) {
            return output.lines()
                    .collect(Collectors.toList());
        }
    }
    private String resolvePythonScriptPath(String filename) {
        File file = new File("C:/Users/yiche/Desktop/mlp" + filename);
        return file.getAbsolutePath();
    }
}
