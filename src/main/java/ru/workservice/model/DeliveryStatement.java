package ru.workservice.model;

import lombok.*;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import static java.util.stream.Collectors.groupingBy;

@NoArgsConstructor
@AllArgsConstructor
@Setter
@Getter
@Entity
@Table(name = "DELIVERY_STATEMENTS")
public final class DeliveryStatement {

    @Id
    @GeneratedValue
    private Long id;
    private String number;
    private Contract contract;
    @OneToMany(fetch = FetchType.EAGER, orphanRemoval = true,
            mappedBy = "deliveryStatement", cascade = CascadeType.ALL)
    private List<DeliveryStatementRow> rows = new ArrayList<>();

    public DeliveryStatement(String number, Contract contract, List<DeliveryStatementRow> rows) {
        this.number = number;
        this.contract = contract;
        this.rows = rows;
    }


    public DeliveryStatementRow getRowByProductAndPeriod(String productName, int year) {
        return rows.stream()
                .filter(ds -> ds.getPeriod() == year && ds.getProductName().equals(productName))
                .findFirst()
                .get();
    }

    public boolean isClosed() {
        return rows.stream().allMatch(DeliveryStatementRow::isClosed);
    }

    public Map<Integer, List<DeliveryStatementRow>> getNotDeliveredProductsByPeriod() {
        return rows.stream()
                .filter(row -> !row.isClosed())
                .collect(groupingBy(DeliveryStatementRow::getPeriod));
    }

    @Override
    public String toString() {
        return "Ведомость поставки" + (number != null ? " № " + number : "")
                + " к контракту " + contract.toString();
    }


}
