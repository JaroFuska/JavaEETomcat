import javax.servlet.ServletException;
import javax.servlet.annotation.MultipartConfig;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.Part;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;

@WebServlet(name = "/ExercisesServlet", urlPatterns = {"/ExercisesServlet"})
@MultipartConfig
public class ExercisesServlet extends HttpServlet {
    DbManager dbManager = new DbManager();
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        int exercises = dbManager.getExercises();
        try (PrintWriter out = response.getWriter()) {
            out.println("<html>\n" +
                    "  <head>\n" +
                    "    <title>Exercises</title>\n" +
                    "<h1>Exercises</h1>\n" +
                    "  </head>\n" +
                    "  <body>");
            for (int i = 1; i <= exercises; i++) {
                out.println("<a href='exercise.jsp?ex=" + i + "'" +
                        ">" + "Exercise " + i + "</a><br>");
                out.println("<hr>");
            }
            out.println("</body>\n" +
                    "</html>");
        }
    }

    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {

    }
}
