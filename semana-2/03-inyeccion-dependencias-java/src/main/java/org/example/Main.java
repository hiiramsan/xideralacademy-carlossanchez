package org.example;

import org.example.services.EmailService;
import org.example.services.NotificationService;
import org.example.services.SMSService;

//TIP To <b>Run</b> code, press <shortcut actionId="Run"/> or
// click the <icon src="AllIcons.Actions.Execute"/> icon in the gutter.
public class Main {
    public static void main(String[] args) {
        // la decision de que servicio usar la tomamos aqui, delegamos la responsabilidad
        // para evitar alto acoplamiento y aplicar single responsibiity
        NotificationService emailService = new EmailService();
        OrderProcessor processor = new OrderProcessor(emailService);
        processor.processOrder("ORD-123");

        // si quisieramos usar otro servicio, desde aqui lo podemos asignar, sin tener que modificar el OrderProcessor
        NotificationService smsService = new SMSService();
        OrderProcessor processor2 = new OrderProcessor(smsService);
        processor2.processOrder("ORD-456");

        /** Algo que aprendi: al tener multiples servicios, es deicr, mas clases que inyectar,
         resulta tedioso instanciarlas y asignarlas. Por eso frameworks como Spring facilitan esto
         agregando un DI Container, donde se registran las dependencias
        */

    }
}