Current design patterns and their practical impacts

Prerequisites: Installed Java 21 and Maven 3.9.3+ 

All theory submodules can be started with `mvn clean test`.
All practice submodules can be started with `mvn clean package && java -jar target/quarkus-app/quarkus-run.jar`.
Some modules require extra step (like setting credentials), refer to README of that module for more information.

Metrics:

- Time to first byte
- Response time
- Time to first successful request
- Startup time
- Queries per second