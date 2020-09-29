import org.eclipse.jetty.server.Server;
import org.eclipse.jetty.servlet.ServletContextHandler;
import org.eclipse.jetty.servlet.ServletHolder;
import servlet.DispatcherServlet;


/**
 * Created by IntelliJ IDEA.
 *
 * @author: zxj
 * @date: 2020/9/29 10:37
 * Description: .
 */

public class Main {

    public static void main(String[] args) {

        Server server = new Server(8080);

        ServletContextHandler sch = new ServletContextHandler();

        sch.setContextPath("/");

        server.setHandler(sch);

        sch.addServlet(new ServletHolder(new DispatcherServlet()), "/");

        try {
            server.start();
            server.join();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
