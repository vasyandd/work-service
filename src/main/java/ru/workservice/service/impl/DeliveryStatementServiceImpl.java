package ru.workservice.service.impl;

import org.hibernate.Hibernate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.workservice.repository.DeliveryStatementRepository;
import ru.workservice.model.Contract;
import ru.workservice.model.DeliveryStatement;
import ru.workservice.model.DeliveryStatementRow;
import ru.workservice.model.Notification;
import ru.workservice.repository.DeliveryStatementRowRepository;
import ru.workservice.service.DeliveryStatementService;

import java.util.List;
import java.util.stream.Collectors;



@Service
public class DeliveryStatementServiceImpl implements DeliveryStatementService {
    private final DeliveryStatementRepository deliveryStatementRepository;
    private final DeliveryStatementRowRepository deliveryStatementRowRepository;

    public DeliveryStatementServiceImpl(DeliveryStatementRepository deliveryStatementRepository, DeliveryStatementRowRepository deliveryStatementRowRepository) {
        this.deliveryStatementRepository = deliveryStatementRepository;
        this.deliveryStatementRowRepository = deliveryStatementRowRepository;
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
        DeliveryStatementRow deliveryStatementRow = deliveryStatement
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
            for (DeliveryStatementRow row : ds.getRows()) {
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
    public void delete(Long id) {
        deliveryStatementRepository.deleteById(id);
    }

    @Override
    public void deleteDeliveryStatementRow(Long id) {
        deliveryStatementRowRepository.deleteById(id);
    }
}
