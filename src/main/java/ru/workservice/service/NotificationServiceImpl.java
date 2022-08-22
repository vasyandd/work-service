package ru.workservice.service;

import org.springframework.stereotype.Service;
import ru.workservice.repository.NotificationRepository;
import ru.workservice.service.model.Notification;

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
