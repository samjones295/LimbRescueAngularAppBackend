package com.limbrescue.limbrescueangularappbackend.ml;

import java.io.*;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class MultiLayerPerceptron {
    public List<String> run() {
        ProcessBuilder processBuilder = new ProcessBuilder("./batch/mlp.bat");
        processBuilder.redirectErrorStream(true);
        List<String> results = new ArrayList<>();
        try {
            Process process = processBuilder.start();
            results = readProcessOutput(process.getInputStream());
            for (String s : results) {
                System.out.println(s);
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            return results;
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
