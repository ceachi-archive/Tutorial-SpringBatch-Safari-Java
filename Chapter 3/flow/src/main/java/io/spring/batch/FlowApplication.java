package io.spring.batch;

import org.springframework.batch.core.configuration.annotation.EnableBatchProcessing;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
@EnableBatchProcessing

// flow = colectie de stepsL

/*
 * In exemplu asta vreau sa fac un flow(mai multe steps)
 * care sa fie pus la fiecare job
 * 
 * 
 */
public class FlowApplication {

    public static void main(String[] args) {
        SpringApplication.run(FlowApplication.class, args);
    }
}
