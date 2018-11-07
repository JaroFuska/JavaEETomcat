package main;

import dbmanager.DbManager;

import javax.servlet.RequestDispatcher;
import javax.servlet.ServletException;
import javax.servlet.annotation.MultipartConfig;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.HashMap;

@WebServlet(name = "/main.java.main.UserDataServlet", urlPatterns = {"/main.java.main.UserDataServlet"})
@MultipartConfig
public class UserDataServlet extends HttpServlet {
    DbManager dbManager = new DbManager();

    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        User user = (User) request.getSession().getAttribute("user");
        String login = user.getLogin();
        request.setAttribute("firstname", user.getFirst_name());
        request.setAttribute("lastname", user.getLast_name());
        request.setAttribute("email", user.getEmail());
        RequestDispatcher rd = request.getRequestDispatcher("settings.jsp");
        rd.forward(request, response);

    }

    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {

    }
}
