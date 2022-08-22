package ru.workservice.service;

import org.hibernate.Hibernate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.workservice.repository.DeliveryStatementRepository;
import ru.workservice.service.model.Contract;
import ru.workservice.service.model.DeliveryStatement;
import ru.workservice.service.model.Notification;

import java.util.List;
import java.util.stream.Collectors;



@Service
public class DeliveryStatementServiceImpl implements DeliveryStatementService {
    private final DeliveryStatementRepository deliveryStatementRepository;

    public DeliveryStatementServiceImpl(DeliveryStatementRepository deliveryStatementRepository) {
        this.deliveryStatementRepository = deliveryStatementRepository;
    }


    @Override
    public void saveDeliveryStatement(DeliveryStatement deliveryStatement) {
        deliveryStatement.getRows().forEach(row -> row.setDeliveryStatement(deliveryStatement));
        deliveryStatementRepository.save(deliveryStatement);
    }

    @Transactional
    @Override
    public void updateDeliveryStatement(Notification notification) {
        DeliveryStatement deliveryStatement = getDeliveryStatementsByContract(notification.getContract());
        DeliveryStatement.Row deliveryStatementRow = deliveryStatement
                .getRowByProductAndPeriod(notification.getProductName(), notification.getDate().getYear());
        deliveryStatementRow.increaseActualProductQuantity(notification.getDate().getMonth(), notification.getProductQuantity());
        deliveryStatementRow.addNotification(notification);
        notification.setDeliveryStatementRow(deliveryStatementRow);
        deliveryStatementRepository.save(deliveryStatement);
    }

    @Override
    public DeliveryStatement getDeliveryStatementsByContract(Contract contract) {
        return deliveryStatementRepository
                .findByContract_ContractNumberAndContract_AdditionalAgreementAndContract_ContractDate(contract.getContractNumber(),
                        contract.getAdditionalAgreement(), contract.getContractDate());
    }

    @Override
    public List<DeliveryStatement> getAllDeliveryStatementsWithoutNotifications() {
        return deliveryStatementRepository.findAll();
    }

    @Transactional
    @Override
    public List<DeliveryStatement> getAllDeliveryStatementsWithNotifications() {
        List<DeliveryStatement> result = deliveryStatementRepository.findAll();
        for (DeliveryStatement ds : result) {
            for (DeliveryStatement.Row row : ds.getRows()) {
                Hibernate.initialize(row.getNotifications());
            }
        }
        return result;
    }

    @Override
    public List<DeliveryStatement> getOpenDeliveryStatements() {
        return getAllDeliveryStatementsWithoutNotifications().stream()
                .filter(d -> !d.isClosed())
                .collect(Collectors.toList());
    }

    @Override
    public void deleteAll() {
        deliveryStatementRepository.deleteAll();
    }
}
