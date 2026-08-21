# Inversion of Control (IoC) - Dependency Injection

#### Carlos Hiram Sanchez Meneses
#### 03-inyeccion-dependencias-java

## Problema

Al principio, `OrderProcessor` creaba su propia dependencia directamente:

```java
class OrderProcessor {
    private EmailService emailService = new EmailService();
    ...
}
```

Esto genera alto acoplamiento ya que OrderProcessor queda atado a una implementación concreta y si en el futuro se necesita notificar por SMS en vez de email, hay que modificar el codigo interno de OrderProcessor

Además, esto rompe el principio de Single Responsibility ya que la clase no solo procesa órdenes, también decide cómo construir sus propias dependencias.

## Solución

1. **Utilizar una interfaz**, no una implementación concreta:

```java
interface NotificationService {
    void send(String message);
}
```

`EmailService` y `SMSService` implementan esta interfaz.

2. **Inyectar la dependencia por constructor**, en vez de crearla internamente:

```java
class OrderProcessor {
    private final NotificationService notificationService;

    public OrderProcessor(NotificationService notificationService) {
        this.notificationService = notificationService;
    }

    public void processOrder(String orderId) {
        notificationService.send("Tu orden " + orderId + " fue registrada!");
    }
}
```

Ahora `OrderProcessor` no sabe (ni le importa) qué implementación recibe. Solo espera "algo" que cumpla el contrato de `NotificationService`.

3. **La decisión se toma afuera**, en `Main`:

```java
NotificationService emailService = new EmailService();
OrderProcessor processor = new OrderProcessor(emailService);
processor.processOrder("ORD-123");

NotificationService smsService = new SMSService();
OrderProcessor processor2 = new OrderProcessor(smsService);
processor2.processOrder("ORD-456");
```

## Beneficios

- **Bajo acoplamiento**: `OrderProcessor` nunca cambia, sin importar qué tipo de notificación se use.
- **Responsabilidad única**: `Main` decide qué implementación usar; `OrderProcessor` solo procesa órdenes.
- **Fácil de testear**: se puede inyectar una implementación falsa (mock) de `NotificationService` para probar `OrderProcessor` sin enviar emails o SMS reales.
- **Fácil de extender**: agregar un nuevo canal de notificación (push, WhatsApp, etc.) solo requiere crear una nueva clase que implemente `NotificationService`, sin tocar el resto del código.

