package ru.workservice.service;



import ru.workservice.service.model.Contract;
import ru.workservice.service.model.DeliveryStatement;
import ru.workservice.service.model.Notification;

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
                .collect(toMap(d -> d.getContract().toString(), Function.identity()));
    }

    public static Map<String, List<DeliveryStatement>> structureByProduct(List<DeliveryStatement> deliveryStatements) {
        Map<String, List<DeliveryStatement>> result = new HashMap<>();
        for (DeliveryStatement d : deliveryStatements) {
            for (DeliveryStatement.Row row : d.getRows()) {
                result.computeIfAbsent(row.getProductName(),
                        (unused) -> new ArrayList<>()).add(d);
            }
        }
        return result;
    }

    public static Map<DeliveryStatement.Row, List<Notification>> structureNotificationsByDeliveryStatementRow(DeliveryStatement deliveryStatement) {
        return deliveryStatement.getRows().stream()
                .collect(toMap(row -> row, DeliveryStatement.Row::getNotifications));
    }

    public static Map<Contract, Map<Integer, List<DeliveryStatement.Row>>> structureProductsByContractForPeriod(List<DeliveryStatement> deliveryStatements) {
        Map<Contract, Map<Integer, List<DeliveryStatement.Row>>> result = new HashMap<>();
        for (DeliveryStatement ds : deliveryStatements) {
            Map<Integer, List<DeliveryStatement.Row>> products = ds.getNotDeliveredProductsByPeriod();
            result.put(ds.getContract(), products);
        }
        return result;
    }
}

