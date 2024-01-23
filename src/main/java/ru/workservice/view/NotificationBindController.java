package ru.workservice.view;

import javafx.beans.binding.Bindings;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.ChoiceBox;
import javafx.scene.control.ListView;
import net.rgielen.fxweaver.core.FxmlView;
import org.springframework.stereotype.Component;
import ru.workservice.model.entity.Contract;
import ru.workservice.model.entity.DeliveryStatement;
import ru.workservice.model.entity.DeliveryStatementRow;
import ru.workservice.model.entity.Notification;
import ru.workservice.service.DeliveryStatementService;
import ru.workservice.service.NotificationService;
import ru.workservice.util.DeliveryStatements;
import ru.workservice.view.util.Controls;
import ru.workservice.view.util.InformationWindow;
import ru.workservice.view.util.SceneSwitcher;

import java.net.URL;
import java.util.List;
import java.util.Map;
import java.util.ResourceBundle;
import java.util.stream.Collectors;

@Component
@FxmlView("notification_bind.fxml")
public class NotificationBindController implements Initializable {
    private final NotificationService notificationService;
    private final SceneSwitcher sceneSwitcher;
    private final DeliveryStatementService deliveryStatementService;
    private final ObservableList<Contract> contracts = FXCollections.observableArrayList();
    private final ObservableList<String> products = FXCollections.observableArrayList();
    private Map<Contract, List<DeliveryStatementRow>> productsByContract;

    @FXML
    private ChoiceBox<Contract> contractBox;
    @FXML
    private ChoiceBox<String> productBox;
    @FXML
    private Button bindButton;
    @FXML
    private Button deleteButton;

    @FXML
    private ListView<Notification> notificationListView;

    public NotificationBindController(NotificationService notificationService, SceneSwitcher sceneSwitcher, DeliveryStatementService deliveryStatementService) {
        this.notificationService = notificationService;
        this.sceneSwitcher = sceneSwitcher;
        this.deliveryStatementService = deliveryStatementService;
    }

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        setFieldsOptions();
        addListenerForChoiceBoxFields();
    }

    private void setFieldsOptions() {
        List<DeliveryStatement> deliveryStatements = deliveryStatementService.getOpenDeliveryStatements();
        productsByContract = DeliveryStatements.structureProductsByContract(deliveryStatements);
        contracts.addAll(deliveryStatements.stream()
                .map(DeliveryStatement::getContract)
                .distinct()
                .collect(Collectors.toList()));
        notificationListView.getItems().addAll(notificationService.findAllStub());
        notificationListView.disableProperty().set(true);
        contractBox.setItems(contracts);
        productBox.setItems(products);
        bindButton.disableProperty().bind(Bindings.isEmpty(notificationListView.getSelectionModel().getSelectedItems()));
        deleteButton.disableProperty().bind(Bindings.isEmpty(notificationListView.getSelectionModel().getSelectedItems()));
    }

    private void updateNotifications(){
        notificationListView.getItems().clear();
        notificationListView.getItems().addAll(notificationService.findAllStub());
    }

    private void addListenerForChoiceBoxFields() {
        contractBox.getSelectionModel().selectedItemProperty().addListener((observable, oldValue, newValue) -> {
            if (!contracts.isEmpty()) {
                products.clear();
                Contract contract = observable.getValue();
                products.addAll(productsByContract.get(contract).stream()
                        .map(DeliveryStatementRow::getProductName)
                        .distinct()
                        .collect(Collectors.toList()));
            }
        });
        productBox.getSelectionModel().selectedItemProperty().addListener(((observableValue, newValue, oldValue) -> {
            String selectedProduct = observableValue.getValue();
            if (selectedProduct == null) {
                notificationListView.disableProperty().set(true);
                notificationListView.getSelectionModel().clearSelection();
            } else {
                notificationListView.disableProperty().set(false);
            }
        }));
    }

    public void bindNotification() {
        try {
            Notification notification = notificationListView.getSelectionModel().getSelectedItem();
            notification.setStub(false);
            notification.setContract(contractBox.getSelectionModel().getSelectedItem());
            notification.setProductName(productBox.getSelectionModel().getSelectedItem());
            notificationService.updateNotification(notification);
            InformationWindow.viewSuccessSaveWindow("Извещение вбито!");
            contracts.clear();
            updateNotifications();
        } catch (Exception e) {
            InformationWindow.viewFailMessageWindow("Не получилось вбить извещение");
        }
    }

    public void deleteNotification() {
        try {
            notificationService.delete(
                    notificationListView.getSelectionModel().getSelectedItem()
            );
            updateNotifications();
            InformationWindow.viewSuccessSaveWindow("Сообщение удалено!");
        } catch (Exception e) {
            InformationWindow.viewFailMessageWindow("Не получилось удалить извещение");
        }
    }

    public void viewMainMenu(ActionEvent event) {
        contracts.clear();
        sceneSwitcher.switchSceneTo(MainMenuController.class, event);
    }
}
