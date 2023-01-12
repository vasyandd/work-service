package ru.workservice.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.workservice.repository.NotificationRepository;
import ru.workservice.model.entity.Notification;

@Service
@RequiredArgsConstructor
public class NotificationService {
    private final NotificationRepository notificationRepository;
    private final DeliveryStatementService deliveryStatementService;


    public void saveNotification(Notification notification) {
        deliveryStatementService.updateDeliveryStatement(notification);
        notificationRepository.save(notification);
    }


}
