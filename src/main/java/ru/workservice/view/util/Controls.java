package ru.workservice.view.util;

import javafx.scene.control.Control;

public class Controls {

    public static void setVisibleForFields(boolean isVisible, Control... fields) {
        for (Control f : fields) {
            f.setVisible(isVisible);
        }
    }
}
