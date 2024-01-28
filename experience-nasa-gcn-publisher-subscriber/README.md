**NASA GCN Publisher / Subscriber example**

Subscribe to NASA GCN Kafka and consume published events.

Steps:
1. Sign in https://gcn.nasa.gov/quickstart with your account (like Google account)
2. Select 'gcn.classic.text.SWIFT_ACTUAL_POINTDIR' topic that you would like to consume. 
   Alternatively select any topic, but replace the topic in app properties and in `@Incoming("gcn.classic.text.SWIFT_ACTUAL_POINTDIR")`
3. Export received credentials as environment variables `export GCN_CLIENT_ID=my-id` and `export GCN_CLIENT_SECRET=my-secret`
4. Create JAR `mvn clean package`
5. Start app and wait `java -jar target/quarkus-app/quarkus-run.jar`

References:

- https://gcn.nasa.gov/
- https://mediaspace.illinois.edu/media/t/1_8c6330x7
- https://www.datastreamingawards.io/winners/nasa-jpl
- https://www.confluent.io/blog/cloud-services/
- https://www.youtube.com/watch?v=Wmw4C1D7utI