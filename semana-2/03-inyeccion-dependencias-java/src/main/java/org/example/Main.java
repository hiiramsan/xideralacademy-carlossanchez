package org.example;

import org.example.services.EmailService;
import org.example.services.NotificationService;
import org.example.services.SMSService;

//TIP To <b>Run</b> code, press <shortcut actionId="Run"/> or
// click the <icon src="AllIcons.Actions.Execute"/> icon in the gutter.
public class Main {
    public static void main(String[] args) {
        // la decision de que servicio usar la tomamos aqui, delegando la responsabilidad
        // para evitar alto acoplamiento y aplicar single responsibiity
        NotificationService emailService = new EmailService();
        OrderProcessor processor = new OrderProcessor(emailService);
        processor.processOrder("ORD-123");

        NotificationService smsService = new SMSService();
        OrderProcessor processor2 = new OrderProcessor(smsService);
        processor2.processOrder("ORD-456");
    }
}