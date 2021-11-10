package com.limbrescue.limbrescueangularappbackend;

import com.limbrescue.limbrescueangularappbackend.ml.PythonScript;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class LimbRescueAngularAppBackendApplication {

    public static void main(String[] args) throws Exception{
        SpringApplication.run(LimbRescueAngularAppBackendApplication.class, args);
        PythonScript script = new PythonScript();
        script.run();
    }

}
