package servlets;

import dbmanager.DbManager;
import docker.DockerManager;
import main.MdHash;
import main.User;

import javax.servlet.ServletException;
import javax.servlet.annotation.MultipartConfig;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.security.NoSuchAlgorithmException;

@WebServlet(name = "/main.java.servlets.LoginServlet", urlPatterns = {"/main.java.servlets.LoginServlet"})
@MultipartConfig
public class LoginServlet extends HttpServlet {
    DbManager dbManager = new DbManager();

    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
//        TODO - print message about waiting for approval
        String login = request.getParameter("login");
        String pass = null;
        try {
            pass = new MdHash().getHashPass(request.getParameter("password"));
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        }
        response.setContentType("text/plain");
        response.setCharacterEncoding("UTF-8");
        User user = dbManager.login(login, pass);
        request.getSession().setAttribute("user", user);
        if (user != null) {
            response.getWriter().write("success");
        } else {
            response.getWriter().write("fail");
        }

    }

    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {

    }
}
