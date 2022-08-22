package ru.workservice;

import javafx.application.Application;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class WorkServiceSpringBootApplication {
    public static final String STYLES_PATH = WorkServiceSpringBootApplication.class.getResource("styles.css").toExternalForm();

    public static void main(String[] args) {
        Application.launch(WorkServiceFXApplication.class, args);
    }
}
