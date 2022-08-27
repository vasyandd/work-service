package ru.workservice.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import ru.workservice.model.DeliveryStatementRow;

public interface DeliveryStatementRowRepository extends JpaRepository<DeliveryStatementRow, Long> {
}
