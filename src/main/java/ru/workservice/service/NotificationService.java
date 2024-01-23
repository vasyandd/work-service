package ru.workservice.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.workservice.model.entity.DeliveryStatement;
import ru.workservice.repository.NotificationRepository;
import ru.workservice.model.entity.Notification;

import java.util.List;
import java.util.Optional;

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

    @Transactional
    public void updateNotification(Notification notification) {
        Optional<Notification> notificationOpt = notificationRepository.findById(notification.getId());
        if (notificationOpt.isPresent()) {
            Notification savedNotification = notificationOpt.get();
            savedNotification.setStub(notification.getStub());
            savedNotification.setContract(notification.getContract());
            savedNotification.setProductName(notification.getProductName());
            saveNotification(savedNotification);
        }
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
