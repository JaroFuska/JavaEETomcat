package main;

import com.google.gson.Gson;
import dbmanager.DbManager;

import javax.servlet.ServletException;
import javax.servlet.annotation.MultipartConfig;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.ArrayList;

@WebServlet(name = "/main.java.main.ExercisesServlet", urlPatterns = {"/main.java.main.ExercisesServlet"})
@MultipartConfig
public class ExercisesServlet extends HttpServlet {
    DbManager dbManager = new DbManager();
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        String userType = (String) request.getSession().getAttribute("userType");
        ArrayList<String> list = new ArrayList<>();
        if (userType == "teacher") {
            for (int i = 1; i <= dbManager.getExercisesCount(); i++) {
                list.add(i + "");
            }
        } else {
            list = (ArrayList<String>) dbManager.getVisibleExercises();
        }
        String json = new Gson().toJson(list);
        response.setContentType("application/json");
        response.setCharacterEncoding("UTF-8");
        response.getWriter().write(json);
    }

    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {

    }
}
