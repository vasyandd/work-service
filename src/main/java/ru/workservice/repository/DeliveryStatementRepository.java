package ru.workservice.repository;


import ru.workservice.model.entity.DeliveryStatement;

import java.time.LocalDate;
import java.util.List;

public interface DeliveryStatementRepository extends BaseRepository<DeliveryStatement> {

    List<DeliveryStatement> findAllByContract_ContractNumberAndContract_AdditionalAgreementAndContract_ContractDate(
            String number, Integer agreementNumber, LocalDate date);
}
