package ru.workservice.model.entity;

import com.vladmihalcea.hibernate.type.json.JsonStringType;
import lombok.*;
import org.hibernate.annotations.NotFound;
import org.hibernate.annotations.NotFoundAction;
import org.hibernate.annotations.Type;
import org.hibernate.annotations.TypeDef;

import javax.persistence.*;
import java.math.BigInteger;
import java.time.LocalDate;
import java.time.Month;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.IntStream;

@Getter
@Setter
@EqualsAndHashCode
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(name = "DELIVERY_STATEMENT_ROWS")
@TypeDef(name = "json", typeClass = JsonStringType.class)
public class DeliveryStatementRow {

    @Id
    @GeneratedValue
    private Long id;
    private BigInteger priceForOneProduct;
    private String productName;

    @Type(type = "json")
    @Column(columnDefinition = "varchar")
    private Map<Month, Integer> scheduledShipment;

    @Type(type = "json")
    @Column(columnDefinition = "varchar")
    private Map<Month, Integer> actualShipment = new HashMap<>();
    private Integer period;

    @ManyToOne
    @JoinColumn(name = "ds_id")
    private DeliveryStatement deliveryStatement;

    @NotFound(action = NotFoundAction.IGNORE)
    @OneToMany(fetch = FetchType.LAZY,
            mappedBy = "deliveryStatementRow", orphanRemoval = true)
    private List<Notification> notifications;

    public DeliveryStatementRow(BigInteger priceForOneProduct, String productName, Map<Month, Integer> scheduledShipment, Integer period) {
        this.priceForOneProduct = priceForOneProduct;
        this.productName = productName;
        this.scheduledShipment = scheduledShipment;
        this.period = period;
    }

    @Transient
    public int getActualProductQuantity() {
        return actualShipment.values().stream()
                .flatMapToInt(IntStream::of)
                .sum();
    }

    @Transient
    public int getScheduledProductQuantity() {
        return scheduledShipment.values().stream()
                .flatMapToInt(IntStream::of)
                .sum();
    }

    @Transient
    public Map<Month, String> getProductQuantityWithSlash() {
        Map<Month, String> map = new HashMap<>();
        for (Month month : Month.values()) {
            Integer scheduledQuantity = scheduledShipment.get(month);
            Integer actualQuantity = actualShipment.get(month);
            map.put(month, (scheduledQuantity != null ? scheduledQuantity : 0)
                    + "/" + (actualQuantity != null ? actualQuantity : 0));
        }
        return map;
    }

    @Transient
    public boolean isClosed() {
        return getActualProductQuantity() >= getScheduledProductQuantity();
    }

    @Transient
    public boolean isLastWeekBeforeExpired() {
        if (isClosed() && isExpired()) return false;
        int currentYear = LocalDate.now().getYear();
        return period == currentYear
                && lessLastWeekBeforeExpired();
    }

    @Transient
    private boolean lessLastWeekBeforeExpired() {
        Month currentMonth = LocalDate.now().getMonth();
        int currentDay = LocalDate.now().getDayOfMonth();
        return currentDay > 23
                && getActualShipmentValue(currentMonth) < scheduledShipment.get(currentMonth);
    }

    @Transient
    public boolean isExpired() {
        if (isClosed()) return false;
        int currentYear = LocalDate.now().getYear();
        return period <= currentYear
                && expiredMonthExistsBeforeCurrent();
    }

    @Transient
    private boolean expiredMonthExistsBeforeCurrent() {
        int currentMonthCode = LocalDate.now().getMonth().getValue();
        return scheduledShipment.entrySet().stream()
                .anyMatch(e -> e.getKey().getValue() < currentMonthCode
                        && getActualShipmentValue(e.getKey()) < e.getValue());
    }

    private int getActualShipmentValue(Month month) {
        return actualShipment.get(month) == null ? 0 : actualShipment.get(month);
    }

    @Transient
    public void updateActualShipment(Month month, int quantity) {
        actualShipment.put(month, quantity);
    }

    @Transient
    public void updateScheduledShipment(Month month, int quantity) {
        scheduledShipment.put(month, quantity);
    }

    public void increaseActualProductQuantity(Month month, int quantity) {
        actualShipment.merge(month, quantity, Integer::sum);
    }

    public void addNotification(Notification notification) {
        notifications.add(notification);
    }
}

