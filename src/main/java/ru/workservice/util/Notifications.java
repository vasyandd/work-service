package ru.workservice.util;

import ru.workservice.model.entity.Notification;

import java.util.List;
import java.util.stream.Collectors;

public class Notifications {
    public static String mapListNotificationsToString(List<Notification> notifications) {
        return notifications.stream()
                .map(Notification::getViewInformation)
                .collect(Collectors.joining(", "));
    }
}
