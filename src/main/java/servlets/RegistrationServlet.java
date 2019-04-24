package servlets;

import dbmanager.DbManager;
import main.MdHash;

import javax.servlet.ServletException;
import javax.servlet.annotation.MultipartConfig;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.security.NoSuchAlgorithmException;

@WebServlet(name = "/main.java.servlets.RegistrationServlet", urlPatterns = {"/main.java.servlets.RegistrationServlet"})
@MultipartConfig
public class RegistrationServlet extends HttpServlet {
    DbManager dbManager = new DbManager();

    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        String firstname = request.getParameter("firstname");
        String lastname = request.getParameter("lastname");
        String email = request.getParameter("email");
        String login = (request.getParameter("login") ==  null ? email : request.getParameter("login"));
        String pass = null;
        try {
            pass = new MdHash().getHashPass(request.getParameter("password"));
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        }
        if (request.getSession().getAttribute("user") != null) {
            String regReturn = dbManager.dp_users_update(firstname, lastname, pass, login);
            if (regReturn == "OK") {
                response.sendRedirect("settings.jsp");
            } else {
                response.sendRedirect("settings.jsp");
            }
        } else {
            boolean teacher = ((String) request.getParameter("teacher")) != null;


            String regReturn = dbManager.dp_users_insert(firstname, lastname, email, login, pass, teacher);
            if (regReturn == "OK") {
                response.sendRedirect("index.jsp");
            } else {
                response.sendRedirect("registration.jsp");
            }
        }

    }

    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {

    }


}
