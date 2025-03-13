# Notification Service

A scalable notification service that handles multiple notification channels (email, SMS, push notifications) with a clean API interface.

## Features

- Multi-channel notification support
  - Email notifications (using Gmail SMTP)
  - SMS notifications (using Twilio)
  - Push notifications (using Firebase Cloud Messaging)
- RESTful API
- Queue-based processing with Redis
- Notification templates
- Delivery status tracking
- Rate limiting and retry mechanisms

## Tech Stack

- Java 17
- Spring Boot 3.1.x
- Spring Data MongoDB
- Spring Data Redis
- Spring Mail (Gmail SMTP)
- Twilio SDK
- Firebase Admin SDK
- MongoDB (for storing notification templates and delivery status)
- Redis (for queue management and rate limiting)

## Getting Started

1. Clone the repository
2. Make sure you have the following installed:
   - Java 17 or higher
   - Maven
   - MongoDB
   - Redis
3. Configure application properties:
   ```bash
   cp src/main/resources/application.example.yml src/main/resources/application.yml
   ```
4. Build the project:
   ```bash
   mvn clean install
   ```
5. Run the application:
   ```bash
   mvn spring-boot:run
   ```

## API Documentation

### Send Notification

```http
POST /api/v1/notifications
Content-Type: application/json

{
  "channel": "EMAIL",
  "recipient": "user@example.com",
  "template": "WELCOME_EMAIL",
  "data": {
    "userName": "John Doe"
  }
}
```

More API documentation coming soon...

## Configuration Properties

Configure the following properties in `application.yml`:

```yaml
spring:
  data:
    mongodb:
      uri: mongodb://localhost:27017/notification-service
  redis:
    host: localhost
    port: 6379
  mail:
    host: smtp.gmail.com
    port: 587
    username: ${GMAIL_USERNAME}
    password: ${GMAIL_APP_PASSWORD}
    properties:
      mail:
        smtp:
          auth: true
          starttls:
            enable: true

twilio:
  account-sid: your-account-sid
  auth-token: your-auth-token

firebase:
  config-path: path/to/firebase-config.json
```

## Contributing

Please read CONTRIBUTING.md for details on our code of conduct and the process for submitting pull requests.

## License

This project is licensed under the MIT License - see the LICENSE file for details. 