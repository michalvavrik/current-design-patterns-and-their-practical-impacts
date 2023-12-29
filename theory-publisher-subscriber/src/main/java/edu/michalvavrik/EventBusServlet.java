package edu.michalvavrik;

import io.vertx.mutiny.core.eventbus.EventBus;
import io.vertx.mutiny.core.eventbus.Message;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;
import java.io.PrintWriter;

@WebServlet(urlPatterns = "/event-bus")
@ApplicationScoped
public class EventBusServlet extends HttpServlet {

    @Inject
    EventBus bus;

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        // sends 'Ho' to 'my-event' address, that address responses with 'Ho Hey!' which we write to response
        var event = bus.<String>request("my-event", "Ho").map(Message::body).await().indefinitely();
        final PrintWriter writer = resp.getWriter();
        writer.write(event);
        writer.close();
    }
}
