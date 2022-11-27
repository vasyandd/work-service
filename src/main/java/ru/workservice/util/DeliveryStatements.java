package ru.workservice.util;



import ru.workservice.model.entity.Contract;
import ru.workservice.model.entity.DeliveryStatement;
import ru.workservice.model.entity.DeliveryStatementRow;
import ru.workservice.model.entity.Notification;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Function;

import static java.util.stream.Collectors.toMap;

public final class DeliveryStatements {
    private DeliveryStatements() {
    }

    public static Map<String, DeliveryStatement> structureByContract(List<DeliveryStatement> deliveryStatements) {
        return deliveryStatements.stream()
                .collect(toMap(d -> d.getContract().toString() + " (в/п " + d.getNumber() + ")", Function.identity()));
    }

    public static Map<String, List<DeliveryStatement>> structureByProduct(List<DeliveryStatement> deliveryStatements) {
        Map<String, List<DeliveryStatement>> result = new HashMap<>();
        for (DeliveryStatement d : deliveryStatements) {
            for (DeliveryStatementRow row : d.getRows()) {
                result.computeIfAbsent(row.getProductName(),
                        (unused) -> new ArrayList<>()).add(d);
            }
        }
        return result;
    }

    public static Map<DeliveryStatementRow, List<Notification>> structureNotificationsByDeliveryStatementRow(DeliveryStatement deliveryStatement) {
        return deliveryStatement.getRows().stream()
                .collect(toMap(row -> row, DeliveryStatementRow::getNotifications));
    }

    public static Map<Contract, Map<Integer, List<DeliveryStatementRow>>> structureProductsByContractForPeriod(List<DeliveryStatement> deliveryStatements) {
        Map<Contract, Map<Integer, List<DeliveryStatementRow>>> result = new HashMap<>();
        for (DeliveryStatement ds : deliveryStatements) {
            Map<Integer, List<DeliveryStatementRow>> products = ds.getNotDeliveredProductsByPeriod();
            result.put(ds.getContract(), products);
        }
        return result;
    }
}

