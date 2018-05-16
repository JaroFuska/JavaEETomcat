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
        String login = (String) request.getSession().getAttribute("userLogin");
        HashMap<String, String> map = dbManager.getUserData(login);
        request.setAttribute("firstname", map.get("first_name"));
        request.setAttribute("lastname", map.get("last_name"));
        request.setAttribute("email", map.get("email"));
        RequestDispatcher rd = request.getRequestDispatcher("settings.jsp");
        rd.forward(request, response);

    }

    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {

    }
}
