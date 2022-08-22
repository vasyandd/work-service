package ru.workservice.view;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.TextField;
import net.rgielen.fxweaver.core.FxmlView;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import ru.workservice.view.util.SceneSwitcher;

@Component
@FxmlView("login_form.fxml")
public class LoginFormController {

    @Value("${cheat}")
    private String CHEAT_CODE;
    private final SceneSwitcher sceneSwitcher;

    @FXML
    private TextField passwordField;


    public LoginFormController(SceneSwitcher sceneSwitcher) {
        this.sceneSwitcher = sceneSwitcher;
    }

    public void checkPassword(ActionEvent event) {
        if (CHEAT_CODE.equals(passwordField.getText())) {
            sceneSwitcher.switchSceneTo(MainMenuController.class, event);
        }
    }


}
