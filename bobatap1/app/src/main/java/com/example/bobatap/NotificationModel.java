package com.example.bobatap;

import com.google.firebase.firestore.ServerTimestamp;

import java.util.Date;

public class NotificationModel {

    private String id;
    private String notification;
    private String notificationType; // New field for notification type
    @ServerTimestamp
    private Date time;

    public NotificationModel() {
        // Default constructor required for Firebase
    }

    public NotificationModel(String id, String notification, String notificationType, Date time) {
        this.id = id;
        this.notification = notification;
        this.notificationType = notificationType;
        this.time = time;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getNotification() {
        return notification;
    }

    public void setNotification(String notification) {
        this.notification = notification;
    }

    public String getNotificationType() {
        return notificationType;
    }

    public void setNotificationType(String notificationType) {
        this.notificationType = notificationType;
    }

    public Date getTime() {
        return time;
    }

    public void setTime(Date time) {
        this.time = time;
    }
}
