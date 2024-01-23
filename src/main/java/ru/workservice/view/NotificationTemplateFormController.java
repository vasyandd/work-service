package ru.workservice.view;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.DatePicker;
import javafx.scene.control.TextField;
import net.rgielen.fxweaver.core.FxmlView;
import org.springframework.stereotype.Component;
import ru.workservice.model.entity.Contract;
import ru.workservice.model.entity.Notification;
import ru.workservice.service.NotificationService;
import ru.workservice.view.util.InformationWindow;
import ru.workservice.view.util.SceneSwitcher;
import ru.workservice.view.util.TextFieldValidator;

import java.net.URL;
import java.util.ResourceBundle;

@Component
@FxmlView("notification_template_form.fxml")
public class NotificationTemplateFormController {
    private static final String SUCCESS_MESSAGE = "Извещение сохранено!";
    private static final String BAD_MESSAGE = "Что-то выделено красным!";
    private final NotificationService notificationService;
    private final SceneSwitcher sceneSwitcher;
    private final TextFieldValidator textFieldValidator;

    @FXML
    private TextField number;
    @FXML
    private DatePicker date;
    @FXML
    private TextField productQuantity;
    @FXML
    private TextField contract;
    @FXML
    private TextField product;
    @FXML
    private TextField productNumbers;

    public NotificationTemplateFormController(NotificationService notificationService, SceneSwitcher sceneSwitcher, TextFieldValidator textFieldValidator) {
        this.notificationService = notificationService;
        this.sceneSwitcher = sceneSwitcher;
        this.textFieldValidator = textFieldValidator;
    }

    public void saveNotification(ActionEvent event) {
        if (textFieldValidator.fieldsAreValid(number, date.getEditor(), productQuantity)) {
            try {
                Contract contract = new Contract();
                contract.setContractNumber(this.contract.getText().trim());
                Notification notification = new Notification(
                        Integer.parseInt(number.getText()),
                        date.getValue(), product.getText().trim(),
                        Integer.parseInt(productQuantity.getText().trim()),
                        productNumbers.getText().trim(),
                        contract);
                notification.setStub(true);
                notificationService.saveTemplate(notification);
                InformationWindow.viewSuccessSaveWindow(SUCCESS_MESSAGE);
                sceneSwitcher.switchSceneTo(MainMenuController.class, event);
            } catch (Exception e) {
                InformationWindow.viewFailMessageWindow("Ошибка при сохранении");
            }
        } else {
            InformationWindow.viewFailMessageWindow(BAD_MESSAGE);
        }

    }

    public void viewMainMenu(ActionEvent event) {
        sceneSwitcher.switchSceneTo(MainMenuController.class, event);
    }
}
