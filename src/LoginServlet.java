import javax.servlet.RequestDispatcher;
import javax.servlet.ServletException;
import javax.servlet.annotation.MultipartConfig;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

@WebServlet(name = "/LoginServlet", urlPatterns = {"/LoginServlet"})
@MultipartConfig
public class LoginServlet extends HttpServlet {
    DbManager dbManager = new DbManager();
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        String login = request.getParameter("login");
        String pass = request.getParameter("password");
        if (dbManager.login(login, pass)) {
            RequestDispatcher rd = request.getRequestDispatcher("ExercisesServlet");
            rd.forward(request,response);
        } else {
            response.sendRedirect("index.jsp");
        }

    }

    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {

    }
}
