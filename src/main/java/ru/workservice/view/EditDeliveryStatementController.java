package ru.workservice.view;

import javafx.fxml.FXML;
import javafx.scene.control.DatePicker;
import javafx.scene.control.TextField;
import javafx.stage.Stage;
import net.rgielen.fxweaver.core.FxmlView;
import ru.workservice.service.DeliveryStatementService;
import ru.workservice.model.entity.DeliveryStatement;
import ru.workservice.model.entity.DeliveryStatementRow;
import ru.workservice.view.util.InformationWindow;
import ru.workservice.view.util.TextFieldValidator;

import java.math.BigInteger;
import java.time.Month;

import static ru.workservice.view.util.TextFieldValidator.FieldPredicate.*;


@FxmlView("edit_delivery_statement.fxml")
public class EditDeliveryStatementController {
    private DeliveryStatementRow deliveryStatementRow;
    private Stage stage;
    private final TextFieldValidator textFieldValidator = new TextFieldValidator();
    private DeliveryStatementService deliveryStatementService;

    public void setDeliveryStatementService(DeliveryStatementService deliveryStatementService) {
        this.deliveryStatementService = deliveryStatementService;
    }

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
    private TextField productPrice;
    @FXML
    private TextField period;
    @FXML
    private TextField janAct;
    @FXML
    private TextField febAct;
    @FXML
    private TextField marAct;
    @FXML
    private TextField aprAct;
    @FXML
    private TextField mayAct;
    @FXML
    private TextField junAct;
    @FXML
    private TextField julAct;
    @FXML
    private TextField augAct;
    @FXML
    private TextField sepAct;
    @FXML
    private TextField octAct;
    @FXML
    private TextField novAct;
    @FXML
    private TextField decAct;
    @FXML
    private TextField janExp;
    @FXML
    private TextField febExp;
    @FXML
    private TextField marExp;
    @FXML
    private TextField aprExp;
    @FXML
    private TextField mayExp;
    @FXML
    private TextField junExp;
    @FXML
    private TextField julExp;
    @FXML
    private TextField augExp;
    @FXML
    private TextField sepExp;
    @FXML
    private TextField octExp;
    @FXML
    private TextField novExp;
    @FXML
    private TextField decExp;

    public EditDeliveryStatementController() {
    }

    public void initialize() {
        textFieldValidator.addValidatorFor(NOT_NEGATIVE_INTEGER, janAct, janExp, febAct, febExp,
                marAct, marExp, aprAct, aprExp, mayAct, mayExp, junAct, junExp, julAct, julExp, augAct, augExp, sepAct, sepExp,
                octAct, octExp, novAct, novExp, decAct, decExp);
        textFieldValidator.addValidatorFor(NOT_NEGATIVE_BIG_INTEGER, productPrice);
        textFieldValidator.addValidatorFor(NOT_EMPTY, number, productName, contractNumber);
        textFieldValidator.addValidatorFor(POSITIVE_INTEGER_OR_EMPTY, agreementNumber);
        textFieldValidator.addValidatorFor(YEAR, period);
    }

    public void setStage(Stage stage) {
        this.stage = stage;
    }

    public void setDeliveryStatementRow(DeliveryStatementRow deliveryStatementRow) {
        this.deliveryStatementRow = deliveryStatementRow;

        number.setText(String.valueOf(deliveryStatementRow.getDeliveryStatement().getNumber()));
        agreementNumber.setText(String.valueOf(deliveryStatementRow.getDeliveryStatement().getContract().getAdditionalAgreement() == null
        ? "" : deliveryStatementRow.getDeliveryStatement().getContract().getAdditionalAgreement()));
        contractNumber.setText(String.valueOf(deliveryStatementRow.getDeliveryStatement().getContract().getContractNumber()));
        contractDate.setValue(deliveryStatementRow.getDeliveryStatement().getContract().getContractDate());
        productName.setText(deliveryStatementRow.getProductName());
        productPrice.setText(deliveryStatementRow.getPriceForOneProduct().toString());
        period.setText(String.valueOf(deliveryStatementRow.getPeriod()));
        janExp.setText(String.valueOf(deliveryStatementRow.getScheduledShipment().getOrDefault(Month.JANUARY, 0)));
        febExp.setText(String.valueOf(deliveryStatementRow.getScheduledShipment().getOrDefault(Month.FEBRUARY, 0)));
        marExp.setText(String.valueOf(deliveryStatementRow.getScheduledShipment().getOrDefault(Month.MARCH, 0)));
        aprExp.setText(String.valueOf(deliveryStatementRow.getScheduledShipment().getOrDefault(Month.APRIL, 0)));
        mayExp.setText(String.valueOf(deliveryStatementRow.getScheduledShipment().getOrDefault(Month.MAY, 0)));
        junExp.setText(String.valueOf(deliveryStatementRow.getScheduledShipment().getOrDefault(Month.JUNE, 0)));
        julExp.setText(String.valueOf(deliveryStatementRow.getScheduledShipment().getOrDefault(Month.JULY, 0)));
        augExp.setText(String.valueOf(deliveryStatementRow.getScheduledShipment().getOrDefault(Month.AUGUST, 0)));
        sepExp.setText(String.valueOf(deliveryStatementRow.getScheduledShipment().getOrDefault(Month.SEPTEMBER, 0)));
        octExp.setText(String.valueOf(deliveryStatementRow.getScheduledShipment().getOrDefault(Month.OCTOBER, 0)));
        novExp.setText(String.valueOf(deliveryStatementRow.getScheduledShipment().getOrDefault(Month.NOVEMBER, 0)));
        decExp.setText(String.valueOf(deliveryStatementRow.getScheduledShipment().getOrDefault(Month.DECEMBER, 0)));

        janAct.setText(String.valueOf(deliveryStatementRow.getActualShipment().getOrDefault(Month.JANUARY, 0)));
        febAct.setText(String.valueOf(deliveryStatementRow.getActualShipment().getOrDefault(Month.FEBRUARY, 0)));
        marAct.setText(String.valueOf(deliveryStatementRow.getActualShipment().getOrDefault(Month.MARCH, 0)));
        aprAct.setText(String.valueOf(deliveryStatementRow.getActualShipment().getOrDefault(Month.APRIL, 0)));
        mayAct.setText(String.valueOf(deliveryStatementRow.getActualShipment().getOrDefault(Month.MAY, 0)));
        junAct.setText(String.valueOf(deliveryStatementRow.getActualShipment().getOrDefault(Month.JUNE, 0)));
        julAct.setText(String.valueOf(deliveryStatementRow.getActualShipment().getOrDefault(Month.JULY, 0)));
        augAct.setText(String.valueOf(deliveryStatementRow.getActualShipment().getOrDefault(Month.AUGUST, 0)));
        sepAct.setText(String.valueOf(deliveryStatementRow.getActualShipment().getOrDefault(Month.SEPTEMBER, 0)));
        octAct.setText(String.valueOf(deliveryStatementRow.getActualShipment().getOrDefault(Month.OCTOBER, 0)));
        novAct.setText(String.valueOf(deliveryStatementRow.getActualShipment().getOrDefault(Month.NOVEMBER, 0)));
        decAct.setText(String.valueOf(deliveryStatementRow.getActualShipment().getOrDefault(Month.DECEMBER, 0)));
    }

    public void saveDeliveryStatement() {
        if (textFieldValidator.fieldsAreValid(contractNumber, agreementNumber, number, productName, productPrice, period,
                janAct, janExp, febAct, febExp, marAct, marExp, aprAct, aprExp, mayAct, mayExp, junAct, junExp, julAct,
                julExp, augAct, augExp, sepAct, sepExp, octAct, octExp, novAct, novExp, decAct, decExp)) {
            updateDeliveryStatementRow();
            DeliveryStatement deliveryStatement = deliveryStatementRow.getDeliveryStatement();
            deliveryStatementService.saveDeliveryStatement(deliveryStatement);
            InformationWindow.viewSuccessSaveWindow("Ведомость поставки сохранена!");
            stage.close();
        } else {
            InformationWindow.viewFailMessageWindow("Что-то все еще выделено красным");
        }
    }

    private void updateDeliveryStatementRow() {
        deliveryStatementRow.getDeliveryStatement().getContract().setContractDate(contractDate.getValue());
        deliveryStatementRow.getDeliveryStatement().getContract().setContractNumber(contractNumber.getText());
        deliveryStatementRow.getDeliveryStatement().getContract().setAdditionalAgreement(
                agreementNumber.getText().isEmpty() ? null : Integer.parseInt(agreementNumber.getText()));
        deliveryStatementRow.getDeliveryStatement().setNumber(number.getText());
        deliveryStatementRow.setPeriod(Integer.parseInt(period.getText()));
        deliveryStatementRow.setProductName(productName.getText());
        deliveryStatementRow.setPriceForOneProduct(new BigInteger(productPrice.getText()));

        deliveryStatementRow.updateScheduledShipment(Month.JANUARY, Integer.parseInt(janExp.getText()));
        deliveryStatementRow.updateScheduledShipment(Month.FEBRUARY, Integer.parseInt(febExp.getText()));
        deliveryStatementRow.updateScheduledShipment(Month.MARCH, Integer.parseInt(marExp.getText()));
        deliveryStatementRow.updateScheduledShipment(Month.APRIL, Integer.parseInt(aprExp.getText()));
        deliveryStatementRow.updateScheduledShipment(Month.MAY, Integer.parseInt(mayExp.getText()));
        deliveryStatementRow.updateScheduledShipment(Month.JUNE, Integer.parseInt(junExp.getText()));
        deliveryStatementRow.updateScheduledShipment(Month.JULY, Integer.parseInt(julExp.getText()));
        deliveryStatementRow.updateScheduledShipment(Month.AUGUST, Integer.parseInt(augExp.getText()));
        deliveryStatementRow.updateScheduledShipment(Month.SEPTEMBER, Integer.parseInt(sepExp.getText()));
        deliveryStatementRow.updateScheduledShipment(Month.OCTOBER, Integer.parseInt(octExp.getText()));
        deliveryStatementRow.updateScheduledShipment(Month.NOVEMBER, Integer.parseInt(novExp.getText()));
        deliveryStatementRow.updateScheduledShipment(Month.DECEMBER, Integer.parseInt(decExp.getText()));

        deliveryStatementRow.updateActualShipment(Month.JANUARY, Integer.parseInt(janAct.getText()));
        deliveryStatementRow.updateActualShipment(Month.FEBRUARY, Integer.parseInt(febAct.getText()));
        deliveryStatementRow.updateActualShipment(Month.MARCH, Integer.parseInt(marAct.getText()));
        deliveryStatementRow.updateActualShipment(Month.APRIL, Integer.parseInt(aprAct.getText()));
        deliveryStatementRow.updateActualShipment(Month.MAY, Integer.parseInt(mayAct.getText()));
        deliveryStatementRow.updateActualShipment(Month.JUNE, Integer.parseInt(junAct.getText()));
        deliveryStatementRow.updateActualShipment(Month.JULY, Integer.parseInt(julAct.getText()));
        deliveryStatementRow.updateActualShipment(Month.AUGUST, Integer.parseInt(augAct.getText()));
        deliveryStatementRow.updateActualShipment(Month.SEPTEMBER, Integer.parseInt(sepAct.getText()));
        deliveryStatementRow.updateActualShipment(Month.OCTOBER, Integer.parseInt(octAct.getText()));
        deliveryStatementRow.updateActualShipment(Month.NOVEMBER, Integer.parseInt(novAct.getText()));
        deliveryStatementRow.updateActualShipment(Month.DECEMBER, Integer.parseInt(decAct.getText()));
    }

    public void deleteDeliveryStatement() {
        deliveryStatementService.delete(deliveryStatementRow.getDeliveryStatement().getId());
        InformationWindow.viewSuccessSaveWindow("Ведомость поставки удалена!");
        stage.close();
    }

    public void deleteDeliveryStatementRow() {
        deliveryStatementService.deleteDeliveryStatementRow(deliveryStatementRow.getId());
        InformationWindow.viewSuccessSaveWindow("Строка ведомости поставки удалена!");
        stage.close();
    }

}
