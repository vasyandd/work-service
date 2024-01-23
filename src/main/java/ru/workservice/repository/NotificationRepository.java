package ru.workservice.repository;


import org.springframework.data.jpa.repository.JpaRepository;
import ru.workservice.model.entity.Notification;

import java.util.List;

public interface NotificationRepository extends JpaRepository<Notification, Long> {
    List<Notification> findAllByStubIsTrueOrderByContractContractNumber();
}
