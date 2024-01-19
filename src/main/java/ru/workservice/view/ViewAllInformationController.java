package ru.workservice.view;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.collections.transformation.FilteredList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.image.Image;
import javafx.scene.layout.AnchorPane;
import javafx.scene.text.Text;
import javafx.stage.DirectoryChooser;
import javafx.stage.FileChooser;
import javafx.stage.Modality;
import javafx.stage.Stage;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import net.rgielen.fxweaver.core.FxmlView;
import org.springframework.stereotype.Component;
import org.springframework.util.CollectionUtils;
import ru.workservice.WorkServiceFXApplication;
import ru.workservice.model.ExportTableRow;
import ru.workservice.model.entity.DeliveryStatement;
import ru.workservice.model.entity.DeliveryStatementRow;
import ru.workservice.model.entity.Notification;
import ru.workservice.model.entity.ScanFile;
import ru.workservice.service.DeliveryStatementService;
import ru.workservice.service.ExportToPdfService;
import ru.workservice.service.ScanFileService;
import ru.workservice.view.util.InformationWindow;
import ru.workservice.view.util.SceneSwitcher;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.time.Month;
import java.util.*;
import java.util.stream.Collectors;

import static javafx.beans.binding.Bindings.isEmpty;
import static ru.workservice.Constants.ICON;
import static ru.workservice.util.DeliveryStatements.*;
import static ru.workservice.view.util.Style.*;

@Component
@FxmlView("view_all_information.fxml")
public class ViewAllInformationController implements Initializable {
    private final List<DeliveryStatementRow> changedRows = new ArrayList<>();
    private final DeliveryStatementService deliveryStatementService;

    private final ExportToPdfService exportToPdfService;
    private final ObservableList<String> products = FXCollections.observableArrayList();
    private final ObservableList<String> contracts = FXCollections.observableArrayList();
    private final ObservableList<MainTableRow> tableRows = FXCollections.observableArrayList();
    private Map<String, DeliveryStatement> deliveryStatementsByContract;
    private Map<String, Set<DeliveryStatement>> deliveryStatementsByProduct;
    private final Map<DeliveryStatementRow, List<Notification>> notificationsByDeliveryStatement = new HashMap<>();
    private final List<ScanFile> selectedFiles = new ArrayList<>();

    private FilteredList<MainTableRow> filteredTableRows;
    private FilteredList<String> filteredContracts;
    private FilteredList<String> filteredProducts;
    private boolean contractSelectedInListView;
    private final SceneSwitcher sceneSwitcher;
    private final ScanFileService scanFileService;

    @FXML
    private CheckBox viewCompletedRows;
    @FXML
    private CheckBox viewExpiredRows;
    @FXML
    private CheckBox viewLastMonthRows;
    @FXML
    private TextField tableSearchCriteria;
    @FXML
    private TextField listViewSearchCriteria;
    @FXML
    private ListView<String> listOfContractsOrProducts;
    @FXML
    private TableView<MainTableRow> table;
    @FXML
    private Button editDeliveryStatementButton;
    @FXML
    private Button exportTableButton;
    @FXML
    private Button searchInTableButton;
    @FXML
    private Button cancelSearchInTableButton;
    @FXML
    private Button downloadFilesButton;
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

    public ViewAllInformationController(DeliveryStatementService deliveryStatementService, ExportToPdfService exportToPdfService, SceneSwitcher sceneSwitcher, ScanFileService scanFileService) {
        this.deliveryStatementService = deliveryStatementService;
        this.exportToPdfService = exportToPdfService;
        this.sceneSwitcher = sceneSwitcher;
        this.scanFileService = scanFileService;
    }

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        fetchInformationForMainTable();
        setTableAndFieldsOptions();
    }

    private void fetchInformationForMainTable() {
        List<DeliveryStatement> deliveryStatements = deliveryStatementService.getAllDeliveryStatementsWithNotifications();
        deliveryStatementsByProduct = structureByProduct(deliveryStatements);
        deliveryStatements.forEach(ds ->
                notificationsByDeliveryStatement.putAll(structureNotificationsByDeliveryStatementRow(ds)));
        deliveryStatementsByContract = structureByContract(deliveryStatements);
    }

    private void setTableAndFieldsOptions() {
        filteredTableRows = new FilteredList<>(tableRows);
        table.setItems(filteredTableRows);
        products.addAll(deliveryStatementsByProduct.keySet());
        contracts.addAll(new ArrayList<>(deliveryStatementsByContract.keySet()));
        filteredContracts = new FilteredList<>(contracts);
        filteredProducts = new FilteredList<>(products);
        listOfContractsOrProducts.getSelectionModel().selectedItemProperty()
                .addListener(((observableValue, oldValue, newValue) -> {
                    if (observableValue.getValue() != null) {
                        unselectCheckBoxFieldsExceptFor(null);
                        fillTable(observableValue.getValue());
                    } else {
                        setVisibleForFields(false, title);
                        tableRows.clear();
                    }
                    downloadFilesButton.disableProperty().set(selectedFiles.isEmpty());
                }));
        setDisableProperties();
        addListenerForCheckBoxFields();
        setCellValueFactories();
        setRowFactories();
    }

    private void setDisableProperties() {
        editDeliveryStatementButton.disableProperty().bind(isEmpty(table.getSelectionModel().getSelectedItems()));
        exportTableButton.disableProperty().bind(isEmpty(table.getItems()));
        tableSearchCriteria.disableProperty().bind(isEmpty(table.getItems()));
        searchInTableButton.disableProperty().bind(isEmpty(table.getItems()));
        cancelSearchInTableButton.disableProperty().bind(isEmpty(table.getItems()));
    }

    private void addListenerForCheckBoxFields() {
        viewCompletedRows.selectedProperty().addListener(((observableValue, aBoolean, t1) -> {
            if (observableValue.getValue().booleanValue()) {
                unselectCheckBoxFieldsExceptFor(viewCompletedRows);
                filteredTableRows.setPredicate(row -> row.isCompleted);
            } else {
                String selectedItem = listOfContractsOrProducts.getSelectionModel().selectedItemProperty().get();
                fillTable(selectedItem);
            }
        }));
        viewLastMonthRows.selectedProperty().addListener(((observableValue, aBoolean, t1) -> {
            if (observableValue.getValue().booleanValue()) {
                unselectCheckBoxFieldsExceptFor(viewLastMonthRows);
                filteredTableRows.setPredicate(row -> row.isLastMonthNow);
            } else {
                String selectedItem = listOfContractsOrProducts.getSelectionModel().selectedItemProperty().get();
                fillTable(selectedItem);
            }
        }));
        viewExpiredRows.selectedProperty().addListener(((observableValue, aBoolean, t1) -> {
            if (observableValue.getValue().booleanValue()) {
                unselectCheckBoxFieldsExceptFor(viewExpiredRows);
                filteredTableRows.setPredicate(row -> row.isExpired);
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
                    getStyleClass().removeAll(EXPIRED.styleClass(), LAST_WEEK.styleClass(), COMPLETED.styleClass());
                } else if (item.isCompleted) {
                    getStyleClass().removeAll(EXPIRED.styleClass(), LAST_WEEK.styleClass());
                    getStyleClass().add(COMPLETED.styleClass());
                } else if (item.isExpired) {
                    getStyleClass().removeAll(COMPLETED.styleClass(), LAST_WEEK.styleClass());
                    getStyleClass().add(EXPIRED.styleClass());
                } else if (item.isLastMonthNow) {
                    getStyleClass().removeAll(COMPLETED.styleClass(), EXPIRED.styleClass());
                    getStyleClass().add(LAST_WEEK.styleClass());
                } else {
                    getStyleClass().removeAll(EXPIRED.styleClass(), LAST_WEEK.styleClass(), COMPLETED.styleClass());
                }
            }
        });
    }

    public void fillProductsList() {
        setVisibleForFields(false, viewExpiredRows, viewLastMonthRows, viewCompletedRows);
        contractSelectedInListView = false;
        removeListViewPredicate();
        listOfContractsOrProducts.setItems(filteredProducts.sorted(Comparator.naturalOrder()));
    }

    public void fillContractsList() {
        setVisibleForFields(false, viewExpiredRows, viewLastMonthRows, viewCompletedRows);
        contractSelectedInListView = true;
        removeListViewPredicate();
        listOfContractsOrProducts.setItems(filteredContracts.sorted(Comparator.naturalOrder()));
    }

    private void fillTable(String selectedItem) {
        removeTablePredicate();
        if (contractSelectedInListView) {
            fillTableByContract(selectedItem);
        } else {
            fillTableByProduct(selectedItem);
        }
    }

    private void fillTableByContract(String key) {
        tableRows.clear();
        changedRows.clear();
        selectedFiles.clear();
        DeliveryStatement deliveryStatement = deliveryStatementsByContract.get(key);
        List<ScanFile> scanFiles = scanFileService.findAllByDeliveryStatementId(Set.of(deliveryStatement.getId()));
        if (!scanFiles.isEmpty()) {
            selectedFiles.addAll(scanFiles);
        }
        List<MainTableRow> rowsList = new ArrayList<>();
        for (DeliveryStatementRow row : deliveryStatement.getRows()) {
            rowsList.add(mapDeliveryStatementRowToMainTableRow(row, null));
            changedRows.add(row);
        }
        tableRows.addAll(rowsList);
        title.setText(deliveryStatement.toString());
        setVisibleForFields(true, title, viewCompletedRows, viewLastMonthRows, viewExpiredRows);
    }

    private void fillTableByProduct(String key) {
        tableRows.clear();
        changedRows.clear();
        selectedFiles.clear();
        Set<DeliveryStatement> deliveryStatements = deliveryStatementsByProduct.get(key);
        List<ScanFile> scanFiles = scanFileService.findAllByDeliveryStatementId(deliveryStatements.stream()
                .map(DeliveryStatement::getId)
                .collect(Collectors.toSet()));
        if (!scanFiles.isEmpty()) {
            selectedFiles.addAll(scanFiles);
        }
        for (DeliveryStatement deliveryStatement : deliveryStatements) {
            List<MainTableRow> rowsList = new ArrayList<>();
            for (DeliveryStatementRow row : deliveryStatement.getRows()) {
                if (row.getProductName().equals(key)) {
                    String contract = deliveryStatement.getContract().toString();
                    rowsList.add(mapDeliveryStatementRowToMainTableRow(row, contract));
                    changedRows.add(row);
                }
            }
            tableRows.addAll(rowsList);
        }
        title.setText("Все ведомости поставки по изделию " + key);
        setVisibleForFields(true, title, viewCompletedRows, viewLastMonthRows, viewExpiredRows);
    }

    private MainTableRow mapDeliveryStatementRowToMainTableRow(DeliveryStatementRow row, String contract) {
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
                row.isClosed(), row.isExpired(), row.isLastWeekBeforeExpired());
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
            thisStage.getIcons().add(new Image(ICON));
            thisStage.setScene(scene);
            thisStage.setResizable(false);
            thisStage.centerOnScreen();

            EditDeliveryStatementController editController = loader.getController();
            DeliveryStatementRow editableRow = null;
            MainTableRow selectedRow = table.getSelectionModel().getSelectedItem();
            for (DeliveryStatementRow row : changedRows) {
                if ((row.getProductName().equals(selectedRow.productOrContract)
                        || row.getDeliveryStatement().getContract().toString().equals(selectedRow.productOrContract))
                        && row.getPeriod().equals(selectedRow.period)) {
                    editableRow = row;
                }
            }
            editController.initialize();
            editController.setDeliveryStatementRow(editableRow);
            editController.setStage(thisStage);
            editController.setDeliveryStatementService(deliveryStatementService);
            editController.setScanFileService(scanFileService);
            editController.setSelectedFiles(selectedFiles);
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

    public void searchTableRows() {
        filteredTableRows.setPredicate((row) ->
                row.productOrContract
                        .toLowerCase()
                        .matches("(.*)" + tableSearchCriteria.getText().toLowerCase() + "(.*)"));
    }

    public void searchListView() {
        if (contractSelectedInListView) {
            filteredContracts.setPredicate(c -> c.toLowerCase()
                    .matches("(.*)" + listViewSearchCriteria.getText().toLowerCase() + "(.*)"));
        } else {
            filteredProducts.setPredicate(c -> c.toLowerCase()
                    .matches("(.*)" + listViewSearchCriteria.getText().toLowerCase() + "(.*)"));
        }
    }

    public void removeTablePredicate() {
        filteredTableRows.setPredicate(null);
        tableSearchCriteria.setText("");
    }

    public void removeListViewPredicate() {
        filteredContracts.setPredicate(null);
        filteredProducts.setPredicate(null);
        listViewSearchCriteria.setText("");
    }

    public void exportTable() {
        try {
            FileChooser fileChooser = new FileChooser();
            fileChooser.setTitle("Save");
            fileChooser.getExtensionFilters().addAll(new FileChooser.ExtensionFilter("PDF", ".pdf"));
            File file = fileChooser.showSaveDialog(null);
            List<ExportTableRow> exportTableRows = table.getItems().stream()
                    .map(r -> new ExportTableRow(
                            r.productOrContract,
                            String.valueOf(r.period),
                            String.valueOf(r.actualProductQuantity),
                            String.valueOf(r.scheduledProductQuantity),
                            r.productPrice,
                            r.janQuantity,
                            r.febQuantity,
                            r.marQuantity,
                            r.aprQuantity,
                            r.mayQuantity,
                            r.junQuantity,
                            r.julQuantity,
                            r.augQuantity,
                            r.sepQuantity,
                            r.octQuantity,
                            r.novQuantity,
                            r.decQuantity,
                            r.note
                    ))
                    .collect(Collectors.toList());
            exportToPdfService.saveTableToPdf(file, exportTableRows);
            InformationWindow.viewSuccessSaveWindow("Таблица успешно сохранена!");
        } catch (Exception e) {
            InformationWindow.viewFailMessageWindow("При сохранении что-то пошло не так \n" + e.getMessage());
        }
    }

    public void downloadFiles() {
        try {
            DirectoryChooser fileChooser = new DirectoryChooser();
            fileChooser.setTitle("Save");
            File file = fileChooser.showDialog(null);
            scanFileService.downloadFiles(file, selectedFiles);
            InformationWindow.viewSuccessSaveWindow("Файлы успешно сохранены!");
        } catch (Exception e) {
            InformationWindow.viewFailMessageWindow("При сохранении что-то пошло не так \n" + e.getMessage());
        }
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
