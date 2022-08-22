package ru.workservice.view.util;

import javafx.scene.control.Alert;

public class InformationWindow {
    private InformationWindow() {
    }

    public static void viewSuccessSaveWindow(String message) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("Успешное сохранение");
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }

    public static void viewInputDataNotValidWindow(String message) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle("Ошибка ввода");
        alert.setResizable(true);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
}
