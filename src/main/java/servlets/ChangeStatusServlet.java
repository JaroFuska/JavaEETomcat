package servlets;

import dbmanager.DbManager;

import javax.servlet.ServletException;
import javax.servlet.annotation.MultipartConfig;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

@WebServlet(name = "/main.java.servlets.ChangeStatusServlet", urlPatterns = {"/main.java.servlets.ChangeStatusServlet"})
@MultipartConfig
public class ChangeStatusServlet extends HttpServlet {
    DbManager dbManager = new DbManager();
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        String id = request.getParameter("id");
        String status = request.getParameter("status");

        int stat = (Integer.parseInt(status)%2) + 1;

        String ret = dbManager.setStatus(id, stat);

        response.setContentType("text/plain");
        response.setCharacterEncoding("UTF-8");
        response.getWriter().write(ret);
    }

    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {

    }
}
