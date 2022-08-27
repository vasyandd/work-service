package ru.workservice.service;


import ru.workservice.model.Contract;
import ru.workservice.model.DeliveryStatement;
import ru.workservice.model.Notification;

import java.util.List;

public interface DeliveryStatementService {

    void saveDeliveryStatement(DeliveryStatement deliveryStatement);

    void updateDeliveryStatement(Notification notification);

    List<DeliveryStatement> getAllDeliveryStatementsWithNotifications();

    List<DeliveryStatement> getAllDeliveryStatementsWithoutNotifications();

    List<DeliveryStatement> getOpenDeliveryStatements();

    DeliveryStatement getDeliveryStatementsByContract(Contract contract);

    void delete(Long id);

    void deleteDeliveryStatementRow(Long id);

}
