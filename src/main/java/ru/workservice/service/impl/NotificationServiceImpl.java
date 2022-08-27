package ru.workservice.service.impl;

import org.springframework.stereotype.Service;
import ru.workservice.repository.NotificationRepository;
import ru.workservice.model.entity.Notification;
import ru.workservice.service.DeliveryStatementService;
import ru.workservice.service.NotificationService;

@Service
public class NotificationServiceImpl implements NotificationService {
    private final NotificationRepository notificationRepository;
    private final DeliveryStatementService deliveryStatementService;

    public NotificationServiceImpl(DeliveryStatementService deliveryStatementService, NotificationRepository notificationRepository) {
        this.notificationRepository = notificationRepository;
        this.deliveryStatementService = deliveryStatementService;
    }

    @Override
    public void saveNotification(Notification notification) {
        deliveryStatementService.updateDeliveryStatement(notification);
        notificationRepository.save(notification);
    }


}
