package com.pmpml.transit.service;

import com.pmpml.transit.entity.User;
import com.pmpml.transit.enums.NotificationType;
import com.pmpml.transit.repository.NotificationRepository;
import com.pmpml.transit.provider.EmailProvider;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import com.pmpml.transit.entity.Notification;

@Slf4j
@Service
@RequiredArgsConstructor
public class NotificationService {
    private final NotificationRepository notificationRepository;
    private final EmailProvider emailProvider;

    @Async("notificationExecutor")
    public void sendNotification(User user, NotificationType type, String title, String message) {
        try {
            Notification notification = Notification.builder().user(user).type(type).title(title).message(message).build();
            notificationRepository.save(notification);
            emailProvider.sendEmail(user.getEmail(), title, message);
            log.info("Notification sent to {} : {}", user.getEmail(), title);
        } catch (Exception e) {
            log.error("Failed to send notification to {}: {}", user.getEmail(), e.getMessage());
        }
    }
}
