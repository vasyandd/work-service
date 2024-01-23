package ru.workservice.model.entity;

import lombok.*;

import javax.persistence.*;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.TextStyle;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.stream.Collectors;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@EqualsAndHashCode
@Entity
@Table(name = "NOTFIFICATIONS")
public class Notification {

    @Id
    @GeneratedValue
    private Long id;
    private Integer number;
    private LocalDate date;
    private String productName;
    private Integer productQuantity;
    private String productNumbers;
    private Contract contract;
    private Boolean stub = false;

    @ManyToMany
    @JoinTable(
            name = "NOTIFICATIONS_DELIVERY_STATEMENT_ROWS",
            joinColumns = @JoinColumn(name = "notification_id"),
            inverseJoinColumns = @JoinColumn(name = "dsr_id"))
    private List<DeliveryStatementRow> deliveryStatementRows = new ArrayList<>();


    public Notification(Integer number, LocalDate date, String productName,
                        Integer productQuantity, String productNumbers, Contract contract) {
        this.number = number;
        this.date = date;
        this.productName = productName;
        this.productQuantity = productQuantity;
        this.productNumbers = productNumbers;
        this.contract = contract;
    }


    public String getViewInformation() {
        return productQuantity + " шт. ("
                + date.getMonth().getDisplayName(TextStyle.FULL_STANDALONE, new Locale("ru"))
                + ") номера " + productNumbers + (number != null ? " изв. № " + number : "")
                + " от " + date.format(DateTimeFormatter.ofPattern("yyyy.MM.dd"));
    }

    public void addDeliveryStatementRow(DeliveryStatementRow deliveryStatementRow) {
        deliveryStatementRows.add(deliveryStatementRow);
    }

    @Override
    public String toString() {
        return "№ " + number + " от " + date.format(DateTimeFormatter.ofPattern("yyyy.MM.dd"))
                + " к контракту " + contract.getContractNumber()
                + " на изделие " + productName + " (" + productNumbers + "шт.)";
    }
}
