package ru.workservice.view.util;

import javafx.scene.control.Alert;
import javafx.scene.image.Image;
import javafx.stage.Stage;

import static ru.workservice.Constants.ICON;

public class InformationWindow {
    private InformationWindow() {
    }

    public static void viewSuccessSaveWindow(String message) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("Успешное сохранение");
        alert.setHeaderText(null);
        alert.setContentText(message);
        ((Stage)alert.getDialogPane().getScene().getWindow()).getIcons().add(new Image(ICON));
        alert.showAndWait();
    }

    public static void viewFailMessageWindow(String message) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle("Ошибка ввода");
        alert.setResizable(true);
        alert.setHeaderText(null);
        alert.setContentText(message);
        ((Stage)alert.getDialogPane().getScene().getWindow()).getIcons().add(new Image(ICON));
        alert.showAndWait();
    }
}
