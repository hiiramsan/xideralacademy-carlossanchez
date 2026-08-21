package org.example;

import org.example.services.NotificationService;

public class OrderProcessor {

    private final NotificationService notificationService;

    // la dependencia es inyectada, no se crea aqui
    public OrderProcessor(NotificationService notificationService) {
        this.notificationService = notificationService;
    }

    // No sabe que servicio de notificacion usara
    public void processOrder(String orderId) {
        System.out.println("Processing order: " + orderId);
        notificationService.send("Order " + orderId + " has been placed");
    }

}
