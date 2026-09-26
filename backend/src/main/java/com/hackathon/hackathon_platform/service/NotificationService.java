package com.hackathon.hackathon_platform.service;

import com.hackathon.hackathon_platform.dto.NotificationResponse;
import com.hackathon.hackathon_platform.entity.Notification;
import com.hackathon.hackathon_platform.entity.User;
import com.hackathon.hackathon_platform.repository.NotificationRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Map;

@Service
public class NotificationService {

    private final NotificationRepository notificationRepository;

    public NotificationService(NotificationRepository notificationRepository) {
        this.notificationRepository = notificationRepository;
    }

    // Méthode interne, appelée par d'autres services (TeamService, EvaluationService)
    // pour créer une notification — jamais appelée directement depuis un Controller
    public void notify(User recipient, String message) {
        Notification notification = Notification.builder()
                .recipient(recipient)
                .message(message)
                .build();
        notificationRepository.save(notification);
    }

    public List<NotificationResponse> getMyNotifications(User user) {
        return notificationRepository.findByRecipientIdOrderByCreatedAtDesc(user.getId())
                .stream()
                .map(n -> new NotificationResponse(n.getId(), n.getMessage(), n.isRead(), n.getCreatedAt()))
                .toList();
    }

    public long getUnreadCount(User user) {
        return notificationRepository.countByRecipientIdAndIsReadFalse(user.getId());
    }

    @Transactional
    public void markAsRead(Long notificationId, User user) {
        Notification notification = notificationRepository.findById(notificationId)
                .orElseThrow(() -> new IllegalArgumentException("Notification introuvable"));

        if (!notification.getRecipient().getId().equals(user.getId())) {
            throw new IllegalStateException("Cette notification ne vous appartient pas");
        }

        notification.setRead(true);
        notificationRepository.save(notification);
    }

    @Transactional
    public void markAllAsRead(User user) {
        List<Notification> notifications = notificationRepository.findByRecipientIdOrderByCreatedAtDesc(user.getId());
        notifications.forEach(n -> n.setRead(true));
        notificationRepository.saveAll(notifications);
    }
}