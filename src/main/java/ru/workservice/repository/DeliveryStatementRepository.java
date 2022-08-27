package ru.workservice.repository;



import org.springframework.data.repository.CrudRepository;
import ru.workservice.model.DeliveryStatement;

import java.time.LocalDate;
import java.util.List;

public interface DeliveryStatementRepository extends CrudRepository<DeliveryStatement, Long> {

    DeliveryStatement findByContract_ContractNumberAndContract_AdditionalAgreementAndContract_ContractDate(
            String number, Integer agreementNumber, LocalDate date);

    List<DeliveryStatement> findAll();

}
