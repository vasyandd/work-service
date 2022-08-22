package ru.workservice.service.model;

import lombok.*;

import javax.persistence.*;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.TextStyle;
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
    @ManyToOne
    @JoinColumn(name = "ds_row_id")
    private DeliveryStatement.Row deliveryStatementRow;


    public Notification(Integer number, LocalDate date, String productName,
                        Integer productQuantity, String productNumbers, Contract contract) {
        this.number = number;
        this.date = date;
        this.productName = productName;
        this.productQuantity = productQuantity;
        this.productNumbers = productNumbers;
        this.contract = contract;
    }


    public static String mapListNotificationsToString(List<Notification> notifications) {
        return notifications.stream()
                .map(Notification::toString)
                .collect(Collectors.joining(", "));
    }

    @Override
    public String toString() {
        return productQuantity + " шт. ("
                + date.getMonth().getDisplayName(TextStyle.FULL_STANDALONE, new Locale("ru"))
                + ") номера " + productNumbers + (number != null ? " изв. № " + number : "")
                + " от " + date.format(DateTimeFormatter.ofPattern("yyyy.MM.dd"));
    }
}
