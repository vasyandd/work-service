package ru.workservice.view.util;

import javafx.scene.control.TextField;
import org.springframework.stereotype.Component;
import ru.workservice.WorkServiceSpringBootApplication;

import java.util.EnumMap;
import java.util.function.Predicate;

import static ru.workservice.view.util.TextFieldValidator.FieldPredicate.*;

@Component
public final class TextFieldValidator {

    private static final String STYLES_PATH = WorkServiceSpringBootApplication.class.getResource("styles.css").toExternalForm();

    public enum FieldPredicate {
        NOT_EMPTY,
        EMPTY,
        NOT_ZERO,
        ZERO,
        POSITIVE_INTEGER,
        NOT_NEGATIVE_INTEGER,
        POSITIVE_BIG_INTEGER,
        POSITIVE_INTEGER_OR_EMPTY,
        NOT_NEGATIVE_BIG_INTEGER,
        YEAR
    }

    private final EnumMap<FieldPredicate, Predicate<TextField>> predicates = new EnumMap<>(FieldPredicate.class);

    public TextFieldValidator() {
        initializeEnumMap();
    }

    private void initializeEnumMap() {
        predicates.put(NOT_EMPTY, textField -> !textField.getText().trim().isEmpty());
        predicates.put(EMPTY, predicates.get(NOT_EMPTY).negate());
        predicates.put(NOT_ZERO, textField -> !textField.getText().trim().equals("0"));
        predicates.put(ZERO, predicates.get(NOT_ZERO).negate());
        predicates.put(POSITIVE_INTEGER, textField -> {
            try {
                int number = Integer.parseInt(textField.getText().trim());
                return number > 0;
            } catch (NumberFormatException e) {
                return false;
            }
        });
        predicates.put(NOT_NEGATIVE_INTEGER, predicates.get(POSITIVE_INTEGER).or(predicates.get(ZERO)));
        predicates.put(POSITIVE_INTEGER_OR_EMPTY, predicates.get(POSITIVE_INTEGER).or(predicates.get(EMPTY)));
        predicates.put(POSITIVE_BIG_INTEGER, predicates.get(NOT_EMPTY).and(textField -> {
            String number = textField.getText().trim();
            return number.matches("\\d*") && !number.startsWith("-")
                    && !number.equals("0");
        }));
        predicates.put(NOT_NEGATIVE_BIG_INTEGER, predicates.get(POSITIVE_BIG_INTEGER).or(predicates.get(ZERO)));
        predicates.put(YEAR, predicates.get(POSITIVE_INTEGER).and(textField -> {
            int number = Integer.parseInt(textField.getText().trim());
            return number > 2000 && number < 2100;
        }));
    }

    public void addValidatorFor(FieldPredicate predicate, TextField... textFields) {
        for (TextField field : textFields) {
            field.textProperty().addListener((observable, oldValue, newValue) -> {
                if (predicates.get(predicate).test(field)) {
                    field.getStyleClass().removeAll(Style.INVALID_DATA.styleClass());
                } else {
                    field.getStylesheets().add(STYLES_PATH);
                    field.getStyleClass().add(Style.INVALID_DATA.styleClass());
                }
            });
        }
    }

    public boolean fieldsAreValid(TextField... fields) {
        for (TextField field : fields) {
            if (field.getStyleClass().contains(Style.INVALID_DATA.styleClass()))
                return false;
        }
        return true;
    }
}
