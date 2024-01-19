package ru.workservice.service;

import lombok.RequiredArgsConstructor;
import org.hibernate.Hibernate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.workservice.repository.DeliveryStatementRepository;
import ru.workservice.model.entity.Contract;
import ru.workservice.model.entity.DeliveryStatement;
import ru.workservice.model.entity.DeliveryStatementRow;
import ru.workservice.model.entity.Notification;
import ru.workservice.repository.DeliveryStatementRowRepository;

import javax.persistence.EntityNotFoundException;
import java.time.Month;
import java.util.*;
import java.util.stream.Collectors;


@Service
@RequiredArgsConstructor
public class DeliveryStatementService {
    private final DeliveryStatementRepository deliveryStatementRepository;
    private final DeliveryStatementRowRepository deliveryStatementRowRepository;
    private final ScanFileService scanFileService;

    @Transactional
    public void saveDeliveryStatement(DeliveryStatement deliveryStatement) {
        deliveryStatement.getRows().forEach(row -> {
            row.setDeliveryStatement(deliveryStatement);
            deliveryStatementRowRepository.save(row);
        });
        deliveryStatementRepository.save(deliveryStatement);
    }

    @Transactional
    public void updateDeliveryStatement(Notification notification) {
        List<DeliveryStatement> deliveryStatements = getDeliveryStatementsByContract(notification.getContract());
        if (deliveryStatements.isEmpty()) return;
        int lessProductQuantity = notification.getProductQuantity();
        int currentYear = notification.getDate().getYear();
        Month currentMonth;
        boolean needAddNotification = true;
        while (lessProductQuantity != 0) {
            DeliveryStatementRow deliveryStatementRow = getActualDeliveryStatementRow(
                    deliveryStatements,
                    notification.getProductName(),
                    currentYear);
            int addedQuantity = lessProductQuantity;

            if (deliveryStatementRow == null) {
                throw new EntityNotFoundException();
            }

            currentMonth = deliveryStatementRow.getFreeMonth();

            if (currentMonth == null) {
                currentYear++;
                needAddNotification = true;
                continue;
            }

            Integer actualShipment = deliveryStatementRow.getActualShipment().get(currentMonth);
            Integer plannedShipment = deliveryStatementRow.getScheduledShipment().get(currentMonth);

            if (actualShipment == null) actualShipment = 0;
            if ((actualShipment + lessProductQuantity) > plannedShipment) {
                addedQuantity = plannedShipment - actualShipment;
            }

            deliveryStatementRow.increaseActualProductQuantity(currentMonth, addedQuantity);

            if (needAddNotification) {
                deliveryStatementRow.addNotification(notification);
                notification.addDeliveryStatementRow(deliveryStatementRow);
            }

            lessProductQuantity = lessProductQuantity - addedQuantity;
            needAddNotification = false;
        }

    }

    private DeliveryStatementRow getActualDeliveryStatementRow(
            List<DeliveryStatement> deliveryStatements,
            String productName,
            int year) {
        return deliveryStatements.stream()
                .sorted(Comparator.comparing(DeliveryStatement::getNumber).reversed())
                .flatMap(deliveryStatement -> deliveryStatement.getRows().stream())
                .filter(ds -> ds.getPeriod() == year && ds.getProductName().equals(productName))
                .findFirst()
                .orElse(null);
    }

    public List<DeliveryStatement> getDeliveryStatementsByContract(Contract contract) {
        return deliveryStatementRepository.findAllByContract_ContractNumberAndContract_AdditionalAgreementAndContract_ContractDate(
                contract.getContractNumber(), contract.getAdditionalAgreement(), contract.getContractDate());
    }

    public List<DeliveryStatement> getAllDeliveryStatementsWithoutNotifications() {
        return deliveryStatementRepository.findAll();
    }

    @Transactional
    public List<DeliveryStatement> getAllDeliveryStatementsWithNotifications() {
        List<DeliveryStatement> result = deliveryStatementRepository.findAll();
        for (DeliveryStatement ds : result) {
            for (DeliveryStatementRow row : ds.getRows()) {
                Hibernate.initialize(row.getNotifications());
            }
        }
        return result;
    }

    public List<DeliveryStatement> getOpenDeliveryStatements() {
        return getAllDeliveryStatementsWithoutNotifications().stream()
                .filter(d -> !d.isClosed())
                .collect(Collectors.toList());
//        Map<Contract, List<DeliveryStatement>> dsByContract = openDs.stream()
//                .collect(Collectors.groupingBy(DeliveryStatement::getContract));
//        List<DeliveryStatement> result = new ArrayList<>();
//        for (var dsByContractEntry : dsByContract.entrySet()) {
//            if (dsByContractEntry.getValue().size() > 1) {
//                result.add(dsByContractEntry.getValue().stream()
//                                .max(Comparator.comparing(DeliveryStatement::getNumber))
//                                .get());
//            } else {
//                result.add(dsByContractEntry.getValue().get(0));
//            }
//        }
//        return result;
    }

    @Transactional
    public void delete(Long id) {
        scanFileService.deleteByDeliverStatementId(id);
        deliveryStatementRepository.deleteById(id);
    }

    public void deleteDeliveryStatementRow(Long id) {
        deliveryStatementRowRepository.deleteById(id);
    }
}
