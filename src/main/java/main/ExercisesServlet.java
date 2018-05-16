package main;

import dbmanager.DbManager;

import javax.servlet.ServletException;
import javax.servlet.annotation.MultipartConfig;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

@WebServlet(name = "/main.java.main.ExercisesServlet", urlPatterns = {"/main.java.main.ExercisesServlet"})
@MultipartConfig
public class ExercisesServlet extends HttpServlet {
    DbManager dbManager = new DbManager();
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
//        int exercises = dbManager.getExercises();
        String exercises = String.valueOf(dbManager.getExercises());
//        try (PrintWriter out = response.getWriter()) {
//            out.println("<!DOCTYPE html>\n<html>\n" +
//                    "  <head>\n" +
//                    "    <title>Exercises</title>\n" +
//                    "<h1>Exercises</h1>\n" +
//                    "  </head>\n" +
//                    "  <body>");
//            for (int i = 1; i <= exercises; i++) {
//                out.println("<a href='exercise.jsp?ex=" + i + "'" +
//                        ">" + "Exercise " + i + "</a><br>");
//                out.println("<hr>");
//            }
//            out.println("</body>\n" +
//                    "</html>");
//        }
        response.setContentType("text/plain");
        response.setCharacterEncoding("UTF-8");
        response.getWriter().write(exercises);
    }

    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {

    }
}
