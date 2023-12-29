package edu.michalvavrik;

import io.smallrye.mutiny.Uni;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;
import java.io.PrintWriter;

@WebServlet(urlPatterns = "/publisher-subscriber")
@ApplicationScoped
public class PublisherSubscriberServlet extends HttpServlet {

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        // publisher - subscriber
        // strictly speaking this could be flaky as Servlet do not wait for subscribed response
        // but here response, although asynchronous, arrives so quickly it is acceptable as example (as it works)
        Uni.createFrom().item("Published response").subscribe().with(item -> {
            final PrintWriter writer;
            try {
                writer = resp.getWriter();
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
            writer.write(item + " and Subscribed response");
            writer.close();
        });
    }
}
