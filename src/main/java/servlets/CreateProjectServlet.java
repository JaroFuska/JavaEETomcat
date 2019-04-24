package servlets;

import dbmanager.ArangoDBManager;
import dbmanager.DbManager;
import main.User;
import main.XMLClasses.Exercise;

import javax.servlet.ServletException;
import javax.servlet.annotation.MultipartConfig;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.*;
import java.sql.SQLException;

@WebServlet(name = "/main.java.servlets.CreateProjectServlet", urlPatterns = {"/main.java.servlets.CreateProjectServlet"})
@MultipartConfig
public class CreateProjectServlet extends HttpServlet {
    DbManager dbManager = new DbManager();
    ArangoDBManager arangoManager = new ArangoDBManager();

    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        processRequest(request, response);
    }

    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        processRequest(request, response);
    }

    private void processRequest(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        boolean isVersionChange = (request.getParameter("versionChange") != null && request.getParameter("versionChange").equals("true"));
        int exerciseID = (isVersionChange ? Integer.parseInt((String)request.getSession().getAttribute("ex")) : Integer.parseInt(request.getParameter("ex")));
        String rootDir =  "";
        User user = (User) request.getSession().getAttribute("user");
        request.getSession().setAttribute("user", user);
        Exercise exercise = new Exercise(dbManager.dp_exercises_get_config_file(String.valueOf(exerciseID)));
        request.getSession().setAttribute("exercise", exercise);
        int version = (isVersionChange ? Integer.parseInt(request.getParameter("version")) : arangoManager.getUserLastVersion(exerciseID, user.getUser_id()));
        request.getSession().setAttribute("ex", Integer.toString(exerciseID));
        request.getSession().setAttribute("version", Integer.toString(version));
        try {
            rootDir = dbManager.createExercise(exerciseID, user.getUser_id(), version);
        } catch (SQLException e) {
            e.printStackTrace();
        }
        response.setContentType("text/plain");
        response.setCharacterEncoding("UTF-8");
        response.getWriter().write(rootDir);
    }
}
