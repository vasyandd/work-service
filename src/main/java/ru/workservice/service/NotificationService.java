package ru.workservice.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.workservice.repository.NotificationRepository;
import ru.workservice.model.entity.Notification;

import java.util.List;

@Service
@RequiredArgsConstructor
public class NotificationService {
    private final NotificationRepository notificationRepository;
    private final DeliveryStatementService deliveryStatementService;


    @Transactional
    public void saveNotification(Notification notification) {
        deliveryStatementService.updateDeliveryStatement(notification);
        notificationRepository.save(notification);
    }

    public void saveTemplate(Notification notification) {
        notificationRepository.save(notification);
    }

    public List<Notification> findAllStub() {
        return notificationRepository.findAllByStubIsTrueOrderByContractContractNumber();
    }

    public void delete(Notification notification) {
        notificationRepository.delete(notification);
    }

}
