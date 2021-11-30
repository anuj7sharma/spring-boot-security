# spring-boot-security
This repo is to demonstrate the spring boot security configuration. 
Here user will be able to register himself. Confirmation link will be sent to the registered Email.
This confirmation link will have the expiration time.
Once confirmed. User needs to do login to receive the access_token and refresh_token to call other APIs secured through the spring-boot-security framework.

# Topics Covered Here

1. Spring Boot Security
2. Email send mechanism through JavaMailSender using custom email templates.
3. REST API
4. JWT token Authentication and Authorization for other APIs.
5. Custom Exceptions
6. Javax validations

For sending Mails I used MailDev server locally.
Use following command:

$ npm install -g maildev

$ maildev
