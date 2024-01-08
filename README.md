**Current design patterns and their practical impacts**

Prerequisites: _Installed Java 21 and Maven 3.9.3+_ 

How to start:
* All theory submodules can be started with `mvn clean test`.
* All experience submodules can be started with `mvn clean package && java -jar target/quarkus-app/quarkus-run.jar`.
Some modules require extra step (like setting credentials), refer to README of that module for more information.

Metrics:

- Time to first byte
- Response time
- Time to first successful request
- Startup time
- Queries per second
- Response time (max)
- Throughput
- Resident State Size (RSS)
- DORA Metrics https://cloud.google.com/blog/products/devops-sre/using-the-four-keys-to-measure-your-devops-performance