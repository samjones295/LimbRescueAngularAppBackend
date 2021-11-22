package com.limbrescue.limbrescueangularappbackend.ml;

import java.io.*;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class MultiLayerPerceptron implements MachineLearning {
    /**
     * Runs the MLP python script.
     *
     * @return
     *          The list containing the output.
     */
    public List<String> run() {
        ProcessBuilder processBuilder = new ProcessBuilder("./batch/mlp.bat");
        processBuilder.redirectErrorStream(true);
        List<String> results = new ArrayList<>();
        try {
            Process process = processBuilder.start();
            results = readProcessOutput(process.getInputStream());
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            return results;
        }
    }

    /**
     * Parses an output.
     *
     * @param inputStream
     * @return
     *          The list containing the output.
     * @throws IOException
     */
    public List<String> readProcessOutput(InputStream inputStream) throws IOException {
        try (BufferedReader output = new BufferedReader(new InputStreamReader(inputStream))) {
            return output.lines()
                    .collect(Collectors.toList());
        }
    }
}
