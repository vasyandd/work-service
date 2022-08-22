package ru.workservice.view;

import javafx.beans.binding.Bindings;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.AnchorPane;
import javafx.scene.text.Text;
import javafx.stage.Modality;
import javafx.stage.Stage;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import net.rgielen.fxweaver.core.FxWeaver;
import net.rgielen.fxweaver.core.FxmlView;
import org.springframework.stereotype.Component;
import ru.workservice.WorkServiceFXApplication;
import ru.workservice.service.DeliveryStatementService;
import ru.workservice.service.DeliveryStatements;
import ru.workservice.service.model.DeliveryStatement;
import ru.workservice.service.model.Notification;
import ru.workservice.view.util.SceneSwitcher;


import java.io.IOException;
import java.net.URL;
import java.time.Month;
import java.util.*;

import static ru.workservice.view.util.Style.*;

@Component
@FxmlView("view_all_information.fxml")
public class ViewAllInformationController implements Initializable {
    private final List<DeliveryStatement.Row> changedRows = new ArrayList<>();
    private final DeliveryStatementService deliveryStatementService;
    private final ObservableList<String> products = FXCollections.observableArrayList();
    private final ObservableList<String> contracts = FXCollections.observableArrayList();
    private final ObservableList<MainTableRow> rows = FXCollections.observableArrayList();
    private Map<String, DeliveryStatement> deliveryStatementsByContract;
    private Map<String, List<DeliveryStatement>> deliveryStatementsByProduct;
    private final Map<DeliveryStatement.Row, List<Notification>> notificationsByDeliveryStatement = new HashMap<>();
    private final FxWeaver fxWeaver;
    private boolean contractSelectedInListView;
    private final SceneSwitcher sceneSwitcher;

    @FXML
    private CheckBox viewCompletedRows;
    @FXML
    private CheckBox viewExpiredRows;
    @FXML
    private CheckBox viewLastMonthRows;
    @FXML
    private ListView<String> listOfContractsOrProducts;
    @FXML
    private TableView<MainTableRow> table;
    @FXML
    private Button editDeliveryStatementButton;
    @FXML
    private Label title;
    @FXML
    private TableColumn<MainTableRow, String> productOrContractCol;
    @FXML
    private TableColumn<MainTableRow, String> productPriceCol;
    @FXML
    private TableColumn<MainTableRow, Integer> periodCol;
    @FXML
    private TableColumn<MainTableRow, Integer> scheduledProductQuantityCol;
    @FXML
    private TableColumn<MainTableRow, Integer> actualProductQuantityCol;
    @FXML
    private TableColumn<MainTableRow, Integer> janQuantityCol;
    @FXML
    private TableColumn<MainTableRow, Integer> febQuantityCol;
    @FXML
    private TableColumn<MainTableRow, Integer> marQuantityCol;
    @FXML
    private TableColumn<MainTableRow, Integer> aprQuantityCol;
    @FXML
    private TableColumn<MainTableRow, Integer> mayQuantityCol;
    @FXML
    private TableColumn<MainTableRow, Integer> junQuantityCol;
    @FXML
    private TableColumn<MainTableRow, Integer> julQuantityCol;
    @FXML
    private TableColumn<MainTableRow, Integer> augQuantityCol;
    @FXML
    private TableColumn<MainTableRow, Integer> sepQuantityCol;
    @FXML
    private TableColumn<MainTableRow, Integer> octQuantityCol;
    @FXML
    private TableColumn<MainTableRow, Integer> novQuantityCol;
    @FXML
    private TableColumn<MainTableRow, Integer> decQuantityCol;
    @FXML
    private TableColumn<MainTableRow, String> noteCol;

    public ViewAllInformationController(DeliveryStatementService deliveryStatementService, FxWeaver fxWeaver, SceneSwitcher sceneSwitcher) {
        this.deliveryStatementService = deliveryStatementService;
        this.fxWeaver = fxWeaver;
        this.sceneSwitcher = sceneSwitcher;
    }

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        fetchInformationForMainTable();
        setTableAndFieldsOptions();
    }

    private void fetchInformationForMainTable() {
        List<DeliveryStatement> deliveryStatements = deliveryStatementService.getAllDeliveryStatementsWithNotifications();
        deliveryStatementsByProduct = DeliveryStatements.structureByProduct(deliveryStatements);
        deliveryStatements.forEach(ds ->
                notificationsByDeliveryStatement.putAll(DeliveryStatements.structureNotificationsByDeliveryStatementRow(ds)));
        deliveryStatementsByContract = DeliveryStatements.structureByContract(deliveryStatements);
    }

    private void setTableAndFieldsOptions() {
        table.setItems(rows);
        products.addAll(deliveryStatementsByProduct.keySet());
        contracts.addAll(new ArrayList<>(deliveryStatementsByContract.keySet()));
        listOfContractsOrProducts.getSelectionModel().selectedItemProperty()
                .addListener(((observableValue, oldValue, newValue) -> {
                    if (observableValue.getValue() != null) {
                        unselectCheckBoxFieldsExceptFor(null);
                        fillTable(observableValue.getValue());
                    } else {
                        setVisibleForFields(false, title);
                        rows.clear();
                    }
                }));
        editDeliveryStatementButton.disableProperty().bind(Bindings.isEmpty(table.getSelectionModel().getSelectedItems()));
        addListenerForCheckBoxFields();
        setCellValueFactories();
        setRowFactories();
    }

    private void addListenerForCheckBoxFields() {
        viewCompletedRows.selectedProperty().addListener(((observableValue, aBoolean, t1) -> {
            if (observableValue.getValue().booleanValue()) {
                unselectCheckBoxFieldsExceptFor(viewCompletedRows);
                table.getItems().removeIf(row -> !row.isCompleted);
            } else {
                String selectedItem = listOfContractsOrProducts.getSelectionModel().selectedItemProperty().get();
                fillTable(selectedItem);
            }
        }));
        viewLastMonthRows.selectedProperty().addListener(((observableValue, aBoolean, t1) -> {
            if (observableValue.getValue().booleanValue()) {
                unselectCheckBoxFieldsExceptFor(viewLastMonthRows);
                table.getItems().removeIf(row -> !row.isLastMonthNow);
            } else {
                String selectedItem = listOfContractsOrProducts.getSelectionModel().selectedItemProperty().get();
                fillTable(selectedItem);
            }
        }));
        viewExpiredRows.selectedProperty().addListener(((observableValue, aBoolean, t1) -> {
            if (observableValue.getValue().booleanValue()) {
                unselectCheckBoxFieldsExceptFor(viewExpiredRows);
                table.getItems().removeIf(row -> !row.isExpired);
            } else {
                String selectedItem = listOfContractsOrProducts.getSelectionModel().selectedItemProperty().get();
                fillTable(selectedItem);
            }
        }));
    }

    private void setCellValueFactories() {
        productOrContractCol.setCellValueFactory(new PropertyValueFactory<>("productOrContract"));
        productOrContractCol.setSortable(true);
        productPriceCol.setCellValueFactory(new PropertyValueFactory<>("productPrice"));
        actualProductQuantityCol.setCellValueFactory(new PropertyValueFactory<>("actualProductQuantity"));
        scheduledProductQuantityCol.setCellValueFactory(new PropertyValueFactory<>("scheduledProductQuantity"));
        periodCol.setCellValueFactory(new PropertyValueFactory<>("period"));
        periodCol.setSortable(true);
        janQuantityCol.setCellValueFactory(new PropertyValueFactory<>("janQuantity"));
        febQuantityCol.setCellValueFactory(new PropertyValueFactory<>("febQuantity"));
        marQuantityCol.setCellValueFactory(new PropertyValueFactory<>("marQuantity"));
        aprQuantityCol.setCellValueFactory(new PropertyValueFactory<>("aprQuantity"));
        mayQuantityCol.setCellValueFactory(new PropertyValueFactory<>("mayQuantity"));
        junQuantityCol.setCellValueFactory(new PropertyValueFactory<>("junQuantity"));
        julQuantityCol.setCellValueFactory(new PropertyValueFactory<>("julQuantity"));
        augQuantityCol.setCellValueFactory(new PropertyValueFactory<>("augQuantity"));
        sepQuantityCol.setCellValueFactory(new PropertyValueFactory<>("sepQuantity"));
        octQuantityCol.setCellValueFactory(new PropertyValueFactory<>("octQuantity"));
        novQuantityCol.setCellValueFactory(new PropertyValueFactory<>("novQuantity"));
        decQuantityCol.setCellValueFactory(new PropertyValueFactory<>("decQuantity"));
        noteCol.setCellValueFactory(new PropertyValueFactory<>("note"));
        setWrapFields(noteCol, productOrContractCol);
    }

    @SafeVarargs
    private void setWrapFields(TableColumn<MainTableRow, String>... cols) {
        for (TableColumn<MainTableRow, String> col : cols) {
            col.setCellFactory(tc -> {
                TableCell<MainTableRow, String> cell = new TableCell<>();
                Text text = new Text();
                cell.setGraphic(text);
                cell.setPrefHeight(Control.USE_COMPUTED_SIZE);
                text.wrappingWidthProperty().bind(noteCol.widthProperty());
                text.textProperty().bind(cell.itemProperty());
                return cell;
            });
        }
    }

    private void setRowFactories() {
        table.setRowFactory(tv -> new TableRow<>() {
            @Override
            public void updateItem(MainTableRow item, boolean empty) {
                super.updateItem(item, empty);
                if (item == null) {
                    getStyleClass().removeAll(EXPIRED.styleClass(), LAST_MONTH.styleClass(), COMPLETED.styleClass());
                } else if (item.isCompleted) {
                    getStyleClass().removeAll(EXPIRED.styleClass(), LAST_MONTH.styleClass());
                    getStyleClass().add(COMPLETED.styleClass());
                } else if (item.isExpired) {
                    getStyleClass().removeAll(COMPLETED.styleClass(), LAST_MONTH.styleClass());
                    getStyleClass().add(EXPIRED.styleClass());
                } else if (item.isLastMonthNow) {
                    getStyleClass().removeAll(COMPLETED.styleClass(), EXPIRED.styleClass());
                    getStyleClass().add(LAST_MONTH.styleClass());
                } else {
                    getStyleClass().removeAll(EXPIRED.styleClass(), LAST_MONTH.styleClass(), COMPLETED.styleClass());
                }
            }
        });
    }

    public void fillProductsList(ActionEvent event) {
        setVisibleForFields(false, viewExpiredRows, viewLastMonthRows, viewCompletedRows);
        contractSelectedInListView = false;
        listOfContractsOrProducts.setItems(products);
    }

    public void fillContractsList(ActionEvent event) {
        setVisibleForFields(false, viewExpiredRows, viewLastMonthRows, viewCompletedRows);
        contractSelectedInListView = true;
        listOfContractsOrProducts.setItems(contracts);
    }

    private void fillTable(String selectedItem) {
        if (contractSelectedInListView) {
            fillTableByContract(selectedItem);
        } else {
            fillTableByProduct(selectedItem);
        }
    }

    private void fillTableByContract(String key) {
        rows.clear();
        changedRows.clear();
        DeliveryStatement deliveryStatement = deliveryStatementsByContract.get(key);
        List<MainTableRow> rowsList = new ArrayList<>();
        for (DeliveryStatement.Row row : deliveryStatement.getRows()) {
            rowsList.add(mapDeliveryStatementRowToMainTableRow(row, null));
            changedRows.add(row);
        }
        rows.addAll(rowsList);
        title.setText(deliveryStatement.toString());
        setVisibleForFields(true, title, viewCompletedRows, viewLastMonthRows, viewExpiredRows);
    }

    private void fillTableByProduct(String key) {
        rows.clear();
        changedRows.clear();
        List<DeliveryStatement> deliveryStatements = deliveryStatementsByProduct.get(key);
        for (DeliveryStatement deliveryStatement : deliveryStatements) {
            List<MainTableRow> rowsList = new ArrayList<>();
            for (DeliveryStatement.Row row : deliveryStatement.getRows()) {
                if (row.getProductName().equals(key)) {
                    String contract = deliveryStatement.getContract().toString();
                    rowsList.add(mapDeliveryStatementRowToMainTableRow(row, contract));
                    changedRows.add(row);
                }
            }
            rows.addAll(rowsList);
        }
        title.setText("Все ведомости поставки по изделию " + key);
        setVisibleForFields(true, title, viewCompletedRows, viewLastMonthRows, viewExpiredRows);
    }

    private MainTableRow mapDeliveryStatementRowToMainTableRow(DeliveryStatement.Row row, String contract) {
        Map<Month, String> productQuantityByMonth = row.getProductQuantityWithSlash();
        String firstColumnValue = contract == null ? row.getProductName() : contract;
        return new MainTableRow(firstColumnValue, row.getPeriod(),
                row.getActualProductQuantity(), row.getScheduledProductQuantity(),
                row.getPriceForOneProduct().toString(), productQuantityByMonth.get(Month.JANUARY),
                productQuantityByMonth.get(Month.FEBRUARY), productQuantityByMonth.get(Month.MARCH),
                productQuantityByMonth.get(Month.APRIL), productQuantityByMonth.get(Month.MAY),
                productQuantityByMonth.get(Month.JUNE), productQuantityByMonth.get(Month.JULY),
                productQuantityByMonth.get(Month.AUGUST), productQuantityByMonth.get(Month.SEPTEMBER),
                productQuantityByMonth.get(Month.OCTOBER), productQuantityByMonth.get(Month.NOVEMBER),
                productQuantityByMonth.get(Month.DECEMBER),
                Notification.mapListNotificationsToString(notificationsByDeliveryStatement.get(row)),
                row.isClosed(), row.isExpired(), row.isLastMonthNow());
    }

    private void setVisibleForFields(boolean isVisible, Control... fields) {
        for (Control f : fields) {
            f.setVisible(isVisible);
        }
    }

    private void unselectCheckBoxFieldsExceptFor(CheckBox checkBox) {
        CheckBox[] checkBoxes = new CheckBox[]{viewExpiredRows, viewLastMonthRows, viewCompletedRows};
        for (CheckBox f : checkBoxes) {
            if (!f.equals(checkBox)) {
                f.setSelected(false);
            }
        }
    }

    public void editDeliveryStatement(ActionEvent event) {
        try {
            FXMLLoader loader = new FXMLLoader();
            loader.setLocation(WorkServiceFXApplication.class.getResource("view/edit_delivery_statement.fxml"));
            AnchorPane root = loader.load();
            Scene scene = new Scene(root);
            Stage thisStage = new Stage();
            thisStage.setTitle("Лучше не ошибайся епта");
            thisStage.initModality(Modality.WINDOW_MODAL);
            thisStage.setScene(scene);
            thisStage.setResizable(false);
            thisStage.centerOnScreen();

            EditDeliveryStatementController editController = loader.getController();
            DeliveryStatement.Row editableRow = null;
            MainTableRow selectedRow = table.getSelectionModel().getSelectedItem();
            for (DeliveryStatement.Row row : changedRows) {
                if ((row.getProductName().equals(selectedRow.productOrContract)
                || row.getDeliveryStatement().getContract().toString().equals(selectedRow.productOrContract))
                && row.getPeriod().equals(selectedRow.period)) {
                    editableRow = row;
                }
            }
            editController.initialize();
            editController.setDeliveryStatementRow(editableRow);
            editController.setStage(thisStage);
            editController.setSceneSwitcher(sceneSwitcher);
            editController.setDeliveryStatementService(deliveryStatementService);
            thisStage.showAndWait();
            clearMainList();
            sceneSwitcher.switchSceneTo(ViewAllInformationController.class, event);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void viewMainMenu(ActionEvent event) {
        clearMainList();
        sceneSwitcher.switchSceneTo(MainMenuController.class, event);
    }

    private void clearMainList() {
        products.clear();
        contracts.clear();
    }

    @AllArgsConstructor
    @Getter
    @Setter
    public static class MainTableRow {
        private String productOrContract;
        private int period;
        private int actualProductQuantity;
        private int scheduledProductQuantity;
        private String productPrice;
        private String janQuantity;
        private String febQuantity;
        private String marQuantity;
        private String aprQuantity;
        private String mayQuantity;
        private String junQuantity;
        private String julQuantity;
        private String augQuantity;
        private String sepQuantity;
        private String octQuantity;
        private String novQuantity;
        private String decQuantity;
        private String note;
        private boolean isCompleted;
        private boolean isExpired;
        private boolean isLastMonthNow;
    }
}
