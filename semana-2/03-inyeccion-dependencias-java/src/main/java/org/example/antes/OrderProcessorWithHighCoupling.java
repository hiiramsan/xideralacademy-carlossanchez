package org.example.antes;

public class OrderProcessorWithHighCoupling {
    class EmailService {
        public void send(String message) {
            System.out.println("Sending EMAIL: " + message);
        }
    }

    class OrderProcessor {
        private EmailService emailService = new EmailService(); // <-- created directly

        public void processOrder(String orderId) {
            System.out.println("Processing order " + orderId);
            emailService.send("Your order " + orderId + " has been placed!");
        }
    }

//    public class Main {
//        public static void main(String[] args) {
//            OrderProcessor processor = new OrderProcessor();
//            processor.processOrder("ORD-123");
//        }
//    }
}
