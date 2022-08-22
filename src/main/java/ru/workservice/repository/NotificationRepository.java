package ru.workservice.repository;


import org.springframework.data.jpa.repository.JpaRepository;
import ru.workservice.service.model.DeliveryStatement;
import ru.workservice.service.model.Notification;

import java.util.List;

public interface NotificationRepository extends JpaRepository<Notification, Long> {

    List<Notification> findByDeliveryStatementRow(DeliveryStatement.Row row);

}