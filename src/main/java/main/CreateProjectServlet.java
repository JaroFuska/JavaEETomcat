package main;

import dbmanager.DbManager;

import javax.servlet.ServletException;
import javax.servlet.annotation.MultipartConfig;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.*;
import java.sql.SQLException;

@WebServlet(name = "/main.java.main.CreateProjectServlet", urlPatterns = {"/main.java.main.CreateProjectServlet"})
@MultipartConfig
public class CreateProjectServlet extends HttpServlet {
    DbManager dbManager = new DbManager();

    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        processRequest(request, response);
    }

    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        processRequest(request, response);
    }

    private void processRequest(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        int exerciseID = Integer.parseInt(request.getParameter("ex"));
        String rootDir =  "";
        User user = (User) request.getSession().getAttribute("user");
        request.getSession().setAttribute("user", user);
        Exercise exercise = new Exercise(dbManager.dp_exercises_get_config_file(String.valueOf(exerciseID)));
        request.getSession().setAttribute("exercise", exercise);
        int version = dbManager.getUserLastVersion(exerciseID, user.getUser_id());
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
