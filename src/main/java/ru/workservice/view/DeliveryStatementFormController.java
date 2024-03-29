package ru.workservice.view;

import javafx.beans.binding.Bindings;
import javafx.beans.property.IntegerProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.stage.FileChooser;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import net.rgielen.fxweaver.core.FxmlView;
import org.springframework.stereotype.Component;
import org.springframework.util.CollectionUtils;
import ru.workservice.model.entity.Contract;
import ru.workservice.model.entity.DeliveryStatement;
import ru.workservice.model.entity.DeliveryStatementRow;
import ru.workservice.model.entity.ScanFile;
import ru.workservice.service.DeliveryStatementService;
import ru.workservice.service.ScanFileService;
import ru.workservice.view.util.InformationWindow;
import ru.workservice.view.util.SceneSwitcher;
import ru.workservice.view.util.TextFieldValidator;

import java.io.File;
import java.math.BigInteger;
import java.net.URL;
import java.time.LocalDate;
import java.time.Month;
import java.util.*;
import java.util.stream.Collectors;

import static ru.workservice.view.util.TextFieldValidator.FieldPredicate.*;

@Component
@FxmlView("delivery_statement_form.fxml")
public class DeliveryStatementFormController implements Initializable {
    private final ObservableList<TableRowInDeliveryStatementForm> rows = FXCollections.observableArrayList();
    private final DeliveryStatementService deliveryStatementService;
    private final SceneSwitcher sceneSwitcher;
    private final TextFieldValidator textFieldValidator;

    private final ScanFileService scanFileService;

    @FXML
    private ListView<ScanFile> files;

    @FXML
    private Button deleteRowButton;
    @FXML
    private TextField number;
    @FXML
    private TextField agreementNumber;
    @FXML
    private TextField contractNumber;
    @FXML
    private DatePicker contractDate;
    @FXML
    private TextField productName;
    @FXML
    private TextField period;
    @FXML
    private TextField productPrice;
    @FXML
    private TextField janQuantity;
    @FXML
    private TextField febQuantity;
    @FXML
    private TextField marQuantity;
    @FXML
    private TextField aprQuantity;
    @FXML
    private TextField mayQuantity;
    @FXML
    private TextField junQuantity;
    @FXML
    private TextField julQuantity;
    @FXML
    private TextField augQuantity;
    @FXML
    private TextField sepQuantity;
    @FXML
    private TextField octQuantity;
    @FXML
    private TextField novQuantity;
    @FXML
    private TextField decQuantity;
    @FXML
    private TableView<TableRowInDeliveryStatementForm> table;
    @FXML
    private TableColumn<TableRowInDeliveryStatementForm, String> productNameCol;
    @FXML
    private TableColumn<TableRowInDeliveryStatementForm, String> productPriceCol;
    @FXML
    private TableColumn<TableRowInDeliveryStatementForm, Integer> periodCol;
    @FXML
    private TableColumn<TableRowInDeliveryStatementForm, Integer> productQuantityCol;
    @FXML
    private TableColumn<TableRowInDeliveryStatementForm, Integer> janQuantityCol;
    @FXML
    private TableColumn<TableRowInDeliveryStatementForm, Integer> febQuantityCol;
    @FXML
    private TableColumn<TableRowInDeliveryStatementForm, Integer> marQuantityCol;
    @FXML
    private TableColumn<TableRowInDeliveryStatementForm, Integer> aprQuantityCol;
    @FXML
    private TableColumn<TableRowInDeliveryStatementForm, Integer> mayQuantityCol;
    @FXML
    private TableColumn<TableRowInDeliveryStatementForm, Integer> junQuantityCol;
    @FXML
    private TableColumn<TableRowInDeliveryStatementForm, Integer> julQuantityCol;
    @FXML
    private TableColumn<TableRowInDeliveryStatementForm, Integer> augQuantityCol;
    @FXML
    private TableColumn<TableRowInDeliveryStatementForm, Integer> sepQuantityCol;
    @FXML
    private TableColumn<TableRowInDeliveryStatementForm, Integer> octQuantityCol;
    @FXML
    private TableColumn<TableRowInDeliveryStatementForm, Integer> novQuantityCol;
    @FXML
    private TableColumn<TableRowInDeliveryStatementForm, Integer> decQuantityCol;


    public DeliveryStatementFormController(ScanFileService scanFileService,DeliveryStatementService deliveryStatementService, SceneSwitcher sceneSwitcher, TextFieldValidator textFieldValidator) {
        this.deliveryStatementService = deliveryStatementService;
        this.sceneSwitcher = sceneSwitcher;
        this.textFieldValidator = textFieldValidator;
        this.scanFileService = scanFileService;
    }

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        setFieldsOptions();
        setTableOptions();
    }

    private void setFieldsOptions() {
        period.setText(String.valueOf(LocalDate.now().getYear()));
        textFieldValidator.addValidatorFor(NOT_NEGATIVE_INTEGER, janQuantity, febQuantity, marQuantity,
                aprQuantity, mayQuantity, junQuantity, julQuantity, augQuantity,
                sepQuantity, octQuantity, novQuantity, decQuantity);
        textFieldValidator.addValidatorFor(NOT_NEGATIVE_BIG_INTEGER, productPrice);
        textFieldValidator.addValidatorFor(NOT_EMPTY, number, productName, contractNumber);
        textFieldValidator.addValidatorFor(POSITIVE_INTEGER_OR_EMPTY, agreementNumber);
        textFieldValidator.addValidatorFor(YEAR, period);
        files.getSelectionModel().setSelectionMode(SelectionMode.MULTIPLE);
        deleteRowButton.disableProperty().bind(Bindings.isEmpty(table.getSelectionModel().getSelectedItems()));
    }

    private void setTableOptions() {
        table.setItems(rows);
        table.setOnMouseClicked(mouseEvent -> {
            TableRowInDeliveryStatementForm model = table.getSelectionModel().getSelectedItem();
            if (model != null) {
                productName.setText(model.productName);
                productPrice.setText(model.productPrice);
                period.setText(String.valueOf(model.period));
                janQuantity.setText(String.valueOf(model.janQuantity));
                febQuantity.setText(String.valueOf(model.febQuantity));
                marQuantity.setText(String.valueOf(model.marQuantity));
                aprQuantity.setText(String.valueOf(model.aprQuantity));
                mayQuantity.setText(String.valueOf(model.mayQuantity));
                junQuantity.setText(String.valueOf(model.junQuantity));
                julQuantity.setText(String.valueOf(model.julQuantity));
                augQuantity.setText(String.valueOf(model.augQuantity));
                sepQuantity.setText(String.valueOf(model.sepQuantity));
                octQuantity.setText(String.valueOf(model.octQuantity));
                novQuantity.setText(String.valueOf(model.novQuantity));
                decQuantity.setText(String.valueOf(model.decQuantity));
            }
        });
        setCellValueFactory();
    }

    private void setCellValueFactory() {
        productNameCol.setCellValueFactory(new PropertyValueFactory<>("productName"));
        productPriceCol.setCellValueFactory(new PropertyValueFactory<>("productPrice"));
        productQuantityCol.setCellValueFactory(cellData -> cellData.getValue().productQuantityProperty().asObject());
        periodCol.setCellValueFactory(new PropertyValueFactory<>("period"));
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
    }

    public void saveDeliveryStatement(ActionEvent event) {
        try {
            DeliveryStatement deliveryStatement = getDeliveryStatementFromTableView();
            List<ScanFile> scanFiles = files.getItems();
            deliveryStatementService.saveDeliveryStatement(deliveryStatement);
            scanFileService.saveAllByDeliveryStatement(scanFiles, deliveryStatement);
            table.getItems().clear();
            InformationWindow.viewSuccessSaveWindow("Ведомость поставки сохранена!");
            sceneSwitcher.switchSceneTo(MainMenuController.class, event);
        } catch (Exception e) {
            e.printStackTrace();
            InformationWindow.viewFailMessageWindow(e.getMessage());
        }
    }

    private DeliveryStatement getDeliveryStatementFromTableView() {
        if (!textFieldValidator.fieldsAreValid(contractNumber, agreementNumber, number)) {
            throw new RuntimeException("В шапке что-то выделено красным");
        }
        List<DeliveryStatementRow> rows = new ArrayList<>();
        for (TableRowInDeliveryStatementForm d : table.getItems()) {
            Map<Month, Integer> shipment = new HashMap<>();
            shipment.put(Month.JANUARY, d.janQuantity);
            shipment.put(Month.FEBRUARY, d.febQuantity);
            shipment.put(Month.MARCH, d.marQuantity);
            shipment.put(Month.APRIL, d.aprQuantity);
            shipment.put(Month.MAY, d.mayQuantity);
            shipment.put(Month.JUNE, d.junQuantity);
            shipment.put(Month.JULY, d.julQuantity);
            shipment.put(Month.AUGUST, d.augQuantity);
            shipment.put(Month.SEPTEMBER, d.sepQuantity);
            shipment.put(Month.OCTOBER, d.octQuantity);
            shipment.put(Month.NOVEMBER, d.novQuantity);
            shipment.put(Month.DECEMBER, d.decQuantity);
            rows.add(new DeliveryStatementRow(new BigInteger(d.productPrice.trim()), d.productName.trim(),
                    shipment, d.period));
        }
        if (rows.isEmpty()) {
            throw new RuntimeException("Таблица пустая!");
        }
        String currentNumber = number.getText().trim().isEmpty() ? null : number.getText().trim();
        Integer currentAgreementNumber = agreementNumber.getText().trim().isEmpty()
                ? null : Integer.parseInt(agreementNumber.getText().trim());
        Contract contract = new Contract(contractNumber.getText().trim(), contractDate.getValue(), currentAgreementNumber);
        return new DeliveryStatement(currentNumber, contract, rows);
    }

    public void deleteRowInTable(ActionEvent event) {
        table.getItems().removeAll(table.getSelectionModel().getSelectedItems());
    }

    public void addRowInTable(ActionEvent event) {
        if (textFieldValidator.fieldsAreValid(productName, productPrice, period, janQuantity, febQuantity,
                marQuantity, mayQuantity, junQuantity, julQuantity, augQuantity, sepQuantity,
                octQuantity, novQuantity, decQuantity)) {
            TableRowInDeliveryStatementForm row = mapInputDataToTable();
            rows.add(row);
            clearInputFields();
        } else {
            InformationWindow.viewFailMessageWindow("Что-то все еще выделено красным");
        }
    }

    private TableRowInDeliveryStatementForm mapInputDataToTable() {
        return new TableRowInDeliveryStatementForm(productName.getText(),
                Integer.parseInt(period.getText()), productPrice.getText(),
                Integer.parseInt(janQuantity.getText().trim().isEmpty() ? "0" : janQuantity.getText().trim()),
                Integer.parseInt(febQuantity.getText().trim().isEmpty() ? "0" : febQuantity.getText().trim()),
                Integer.parseInt(marQuantity.getText().trim().isEmpty() ? "0" : marQuantity.getText().trim()),
                Integer.parseInt(aprQuantity.getText().trim().isEmpty() ? "0" : aprQuantity.getText().trim()),
                Integer.parseInt(mayQuantity.getText().trim().isEmpty() ? "0" : mayQuantity.getText().trim()),
                Integer.parseInt(junQuantity.getText().trim().isEmpty() ? "0" : junQuantity.getText().trim()),
                Integer.parseInt(julQuantity.getText().trim().isEmpty() ? "0" : julQuantity.getText().trim()),
                Integer.parseInt(augQuantity.getText().trim().isEmpty() ? "0" : augQuantity.getText().trim()),
                Integer.parseInt(sepQuantity.getText().trim().isEmpty() ? "0" : sepQuantity.getText().trim()),
                Integer.parseInt(octQuantity.getText().trim().isEmpty() ? "0" : octQuantity.getText().trim()),
                Integer.parseInt(novQuantity.getText().trim().isEmpty() ? "0" : novQuantity.getText().trim()),
                Integer.parseInt(decQuantity.getText().trim().isEmpty() ? "0" : decQuantity.getText().trim()));
    }

    private void clearInputFields() {
        productPrice.setText("0");
        janQuantity.setText("0");
        febQuantity.setText("0");
        marQuantity.setText("0");
        aprQuantity.setText("0");
        mayQuantity.setText("0");
        junQuantity.setText("0");
        julQuantity.setText("0");
        augQuantity.setText("0");
        sepQuantity.setText("0");
        octQuantity.setText("0");
        novQuantity.setText("0");
        decQuantity.setText("0");
    }

    public void viewMainMenu(ActionEvent event) {
        sceneSwitcher.switchSceneTo(MainMenuController.class, event);
    }


    public void loadFiles() {
        try {
            FileChooser fileChooser = new FileChooser();
            fileChooser.setTitle("Save");
            List<File> chosenFiles = fileChooser.showOpenMultipleDialog(null);
            if (!CollectionUtils.isEmpty(chosenFiles)) {
                this.files.getItems().addAll(scanFileService.createScanFiles(chosenFiles));
            }
        } catch (Exception e) {
            InformationWindow.viewFailMessageWindow("При добавлении файлов что-то пошло не так \n" + e.getMessage());
        }
    }

    public void clearSelectedFiles() {
        List<ScanFile> selectedFiles = files.getSelectionModel().getSelectedItems();
        files.getItems().removeAll(selectedFiles);
    }

    @Getter
    @Setter
    @AllArgsConstructor
    public static class TableRowInDeliveryStatementForm {
        private String productName;
        private int period;
        private String productPrice;
        private int janQuantity;
        private int febQuantity;
        private int marQuantity;
        private int aprQuantity;
        private int mayQuantity;
        private int junQuantity;
        private int julQuantity;
        private int augQuantity;
        private int sepQuantity;
        private int octQuantity;
        private int novQuantity;
        private int decQuantity;

        IntegerProperty productQuantityProperty() {
            return new SimpleIntegerProperty(janQuantity + febQuantity + marQuantity + aprQuantity + mayQuantity
                    + junQuantity + julQuantity + augQuantity + sepQuantity + octQuantity + novQuantity + decQuantity);
        }

    }
}
