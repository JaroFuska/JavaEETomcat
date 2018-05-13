import javax.servlet.RequestDispatcher;
import javax.servlet.ServletException;
import javax.servlet.annotation.MultipartConfig;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.Part;
import java.io.*;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;

@WebServlet(name = "/CreateProjectServlet", urlPatterns = {"/CreateProjectServlet"})
@MultipartConfig
public class CreateProjectServlet extends HttpServlet {
    DbManager dbManager = new DbManager();

    // TODO: 22-Mar-18 there will be differences when this servlet will be called by student
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        int exerciseID = Integer.parseInt(request.getParameter("ex"));
        String rootDir =  "";
        try {
            rootDir = dbManager.createExercise(exerciseID);
        } catch (SQLException e) {
            e.printStackTrace();
        }
        response.setContentType("text/plain");
        response.setCharacterEncoding("UTF-8");
        response.getWriter().write(rootDir);

    }

    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {

    }
}
