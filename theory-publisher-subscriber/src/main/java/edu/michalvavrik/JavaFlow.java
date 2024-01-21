package edu.michalvavrik;

import io.smallrye.mutiny.Uni;
import io.vertx.core.Vertx;
import io.vertx.core.buffer.Buffer;
import io.vertx.core.http.HttpClientRequest;
import io.vertx.core.http.HttpMethod;
import io.vertx.core.http.RequestOptions;
import io.vertx.ext.web.Router;
import io.vertx.ext.web.RoutingContext;
import jakarta.enterprise.event.Observes;
import mutiny.zero.vertxpublishers.VertxPublisher;
import org.eclipse.microprofile.config.inject.ConfigProperty;

import java.util.concurrent.Flow;

public class JavaFlow {

    void setupRouter(@Observes Router router, Vertx vertx, @ConfigProperty(name = "quarkus.http.test-port") int port,
                     @ConfigProperty(name = "quarkus.http.host") String host) {
        router.route("/java-flow-client").handler(event -> event.end("client-response"));
        router.route("/java-flow").handler(event -> useHttpClientToCallClientRoute(vertx, event, port, host));
    }

    private static void useHttpClientToCallClientRoute(Vertx vertx, RoutingContext routingContext, int port, String host) {
        RequestOptions opts = new RequestOptions()
                .setHost(host)
                .setPort(port)
                .setMethod(HttpMethod.GET)
                .setURI("/java-flow-client");
        var client = vertx.createHttpClient();
        Flow.Publisher<Buffer> publisher = VertxPublisher
                .fromFuture(() -> client.request(opts).compose(HttpClientRequest::send));
        publisher.subscribe(new Flow.Subscriber<>() {

            @Override
            public void onSubscribe(Flow.Subscription s) {
                // call client once
                s.request(1L);
            }

            @Override
            public void onNext(Buffer buffer) {
                if (!routingContext.response().ended()) {
                    routingContext.end("server-response-plus-" + buffer);
                }
            }

            @Override
            public void onError(Throwable t) {
                if (!routingContext.failed()) {
                    routingContext.fail(t);
                }
            }

            @Override
            public void onComplete() {
                Uni.createFrom().completionStage(client.close().toCompletionStage()).await().indefinitely();
            }
        });
    }

}
