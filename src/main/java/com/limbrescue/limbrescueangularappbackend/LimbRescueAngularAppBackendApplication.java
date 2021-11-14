package com.limbrescue.limbrescueangularappbackend;

import com.limbrescue.limbrescueangularappbackend.ml.*;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import java.util.Random;

@SpringBootApplication
public class LimbRescueAngularAppBackendApplication {

    public static void main(String[] args) throws Exception{
        SpringApplication.run(LimbRescueAngularAppBackendApplication.class, args);
        SupportVectorMachine svm = new SupportVectorMachine();
        svm.run();
        RandomForest rf = new RandomForest();
        rf.run();
        NaiveBayes nb = new NaiveBayes();
        nb.run();
//        MultiLayerPerceptron mlp = new MultiLayerPerceptron();
//        mlp.run();
        //This is just some tutorial and will be removed.
        PythonScript script = new PythonScript();
        script.run();
    }

}
