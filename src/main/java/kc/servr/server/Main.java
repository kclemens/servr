package kc.servr.server;

import com.google.inject.servlet.GuiceFilter;
import org.eclipse.jetty.server.Server;
import org.eclipse.jetty.server.handler.HandlerList;
import org.eclipse.jetty.server.handler.ResourceHandler;
import org.eclipse.jetty.servlet.DefaultServlet;
import org.eclipse.jetty.servlet.ServletContextHandler;

import java.util.logging.Logger;

/**
 * User: kclemens
 * Date: 8/20/12
 */
public class Main {

    private final static Logger LOG = Logger.getLogger(Main.class.getSimpleName());


    public static void main(String[] args) throws Exception {
        ServletContextHandler servlet_handler = new ServletContextHandler();
        servlet_handler.setContextPath("/");
        servlet_handler.addEventListener(new GuiceServletListener());
        servlet_handler.addFilter(GuiceFilter.class, "/*", null);
        servlet_handler.addServlet(DefaultServlet.class, "/");

        ResourceHandler resourceHandler = new ResourceHandler();
        resourceHandler.setResourceBase("src/main/webapp/"); //TODO: that's not right
        resourceHandler.setWelcomeFiles(new String[] {"index.html"});
        resourceHandler.setDirectoriesListed(true);

        HandlerList handlerList = new HandlerList();
        handlerList.addHandler(resourceHandler);
        handlerList.addHandler(servlet_handler);

        Server server = new Server(8080);
        server.setHandler(handlerList);
        server.start();
        server.join();
    }
}