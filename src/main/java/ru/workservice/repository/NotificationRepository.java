package ru.workservice.repository;


import org.springframework.data.jpa.repository.JpaRepository;
import ru.workservice.model.Notification;

public interface NotificationRepository extends JpaRepository<Notification, Long> {

}
