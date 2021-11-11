package com.limbrescue.limbrescueangularappbackend;

import com.limbrescue.limbrescueangularappbackend.ml.*;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import java.util.Random;

@SpringBootApplication
public class LimbRescueAngularAppBackendApplication {

    public static void main(String[] args) throws Exception{
        SpringApplication.run(LimbRescueAngularAppBackendApplication.class, args);
        //This is just some tutorial and will be removed.
        PythonScript script = new PythonScript();
        script.run();
        MultiLayerPerceptron mlp = new MultiLayerPerceptron();
        mlp.run();
        NaiveBayes nb = new NaiveBayes();
        nb.run();
        RandomForest rf = new RandomForest();
        rf.run();
        SupportVectorMachine svm = new SupportVectorMachine();
        svm.run();
    }

}
