# 🛒 Purchase Tickets Service

Este microservicio es responsable de gestionar el proceso de finalización de compras de tiquetes dentro del ecosistema de **Ticketing System**.

Está construido con **Spring Boot (WebFlux)** y diseñado para ser **totalmente asíncrono** y **no bloqueante**, delegando el procesamiento pesado a través de colas de mensajes (SQS).

---

## 🚀 Características Principales

*   **Arquitectura Reactiva:** Utiliza Project Reactor (Mono/Flux) para un manejo eficiente de recursos.
*   **Procesamiento Asíncrono:** Los endpoints de finalización de compra responden inmediatamente al cliente (`202 Accepted`) y encolan la tarea para procesamiento en segundo plano.
*   **Integración AWS SQS:** Envía mensajes a la cola `manage-order-queue` utilizando el **AWS SDK v2 Async Client**.

---

## 🛠️ Stack Tecnológico

*   **Java:** 25 (Amazon Corretto)
*   **Framework:** Spring Boot 4.0.2 (WebFlux)
*   **Build Tool:** Gradle
*   **Cloud:** AWS SDK v2 (SQS)

---

## 🔌 API Reference

### Finalizar Compra (Async)

Inicia el proceso de finalización de una orden. Este endpoint no espera a que la base de datos se actualice; simplemente confirma que la solicitud fue recibida.

*   **URL:** `/api/v1/purchase/finalize`
*   **Método:** `POST`
*   **Puerto (Docker):** `9091`

#### Request Body

```json
{
  "orderId": "order-123-abc",
  "status": "SOLD" 
}
```
*Nota: El estado puede ser `SOLD` o `COMPLIMENTARY`.*

#### Response

*   **Status:** `202 Accepted`
*   **Body:**
    ```json
    {
      "message": "Purchase finalization in process",
      "orderId": "order-123-abc"
    }
    ```

---

## ⚙️ Configuración (Variables de Entorno)

El servicio se configura mediante variables de entorno, principalmente para conectar con LocalStack o AWS real.

| Variable | Valor por Defecto (Local) | Descripción |
| :--- | :--- | :--- |
| `SERVER_PORT` | `9091` | Puerto del servidor. |
| `AWS_SQS_ENDPOINT` | `http://localhost:4566` | Endpoint de SQS (LocalStack). |
| `AWS_REGION` | `us-east-1` | Región de AWS. |
| `AWS_SQS_QUEUE_URL` | `.../manage-order-queue` | URL completa de la cola SQS. |

---

## 🏃‍♂️ Ejecución Local

### Con Gradle

```bash
./gradlew bootRun
```

### Con Docker Compose

Este servicio es parte del stack principal definido en la raíz del proyecto:

```bash
docker-compose up -d purchase-tickets-service
```

---

## 📦 Flujo de Mensajería

1.  **Entrada:** Petición HTTP POST al controlador.
2.  **Proceso:** El servicio construye un mensaje JSON: `{"orderId":"...", "status":"..."}`.
3.  **Salida:** El mensaje se envía a la cola SQS `manage-order-queue`.
4.  **Consumidor:** Otro servicio (ej. `bookTickets`) escuchará esta cola para actualizar el estado final en la base de datos DynamoDB.
