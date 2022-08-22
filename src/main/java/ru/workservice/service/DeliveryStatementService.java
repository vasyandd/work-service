package ru.workservice.service;


import ru.workservice.service.model.Contract;
import ru.workservice.service.model.DeliveryStatement;
import ru.workservice.service.model.Notification;

import java.util.List;

public interface DeliveryStatementService {

    void saveDeliveryStatement(DeliveryStatement deliveryStatement);

    void updateDeliveryStatement(Notification notification);

    List<DeliveryStatement> getAllDeliveryStatementsWithNotifications();

    List<DeliveryStatement> getAllDeliveryStatementsWithoutNotifications();

    List<DeliveryStatement> getOpenDeliveryStatements();

    DeliveryStatement getDeliveryStatementsByContract(Contract contract);

    void deleteAll();

}
