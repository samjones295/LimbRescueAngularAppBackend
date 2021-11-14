package com.limbrescue.limbrescueangularappbackend.ml;

import org.python.antlr.ast.Str;

import java.io.*;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class NaiveBayes {
    public List<String> run() {
        ProcessBuilder processBuilder = new ProcessBuilder("./batch/nb.bat");
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
        File file = new File("C:/Users/yiche/Desktop/nb" + filename);
        return file.getAbsolutePath();
    }
}
