package org.example.services;

public class SMSService implements NotificationService {
    @Override
    public void send(String message) {
        System.out.println("SMS service says: " + message);
    }
}
