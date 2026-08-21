 package org.example.services;

public class EmailService implements NotificationService{
    @Override
    public void send(String message) {
        System.out.println("Email service says: " + message);
    }
}
