package ru.workservice.view;

import javafx.event.ActionEvent;
import net.rgielen.fxweaver.core.FxmlView;
import org.springframework.stereotype.Component;
import ru.workservice.view.util.SceneSwitcher;


@Component
@FxmlView("main_menu.fxml")
public class MainMenuController {
    private final SceneSwitcher switcher;


    public MainMenuController(SceneSwitcher switcher) {
        this.switcher = switcher;
    }


    public void switchSceneToNotificationForm(ActionEvent event) {
        switcher.switchSceneTo(NotificationFormController.class, event);
    }

    public void switchSceneToDeliveryStatementForm(ActionEvent event) {
        switcher.switchSceneTo(DeliveryStatementFormController.class, event);
    }

    public void switchSceneToViewAllInformation(ActionEvent event) {
        switcher.switchSceneTo(ViewAllInformationController.class, event);
    }

    public void switchSceneToNotificationTemplateForm(ActionEvent event) {
        switcher.switchSceneTo(NotificationTemplateFormController.class, event);
    }

    public void switchSceneToNotificationBind(ActionEvent event) {
        switcher.switchSceneTo(NotificationBindController.class, event);
    }


}
