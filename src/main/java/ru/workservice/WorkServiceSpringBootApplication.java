package ru.workservice;

import javafx.application.Application;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class WorkServiceSpringBootApplication {

    public static void main(String[] args) {
        Application.launch(WorkServiceFXApplication.class, args);
    }
}
