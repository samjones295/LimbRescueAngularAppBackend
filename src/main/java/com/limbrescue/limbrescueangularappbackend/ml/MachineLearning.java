package com.limbrescue.limbrescueangularappbackend.ml;

import java.io.IOException;
import java.io.InputStream;
import java.util.List;

public interface MachineLearning {
    //The "finally" clause returns the output.
    List<String> run();
    List<String> readProcessOutput(InputStream inputStream) throws IOException;
}
