package servlets;

import dbmanager.ArangoDBManager;
import dbmanager.DbManager;
import main.User;

import javax.servlet.ServletException;
import javax.servlet.annotation.MultipartConfig;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.*;

@WebServlet(name = "/main.java.servlets.CreateNewVersionServlet", urlPatterns = {"/main.java.servlets.CreateNewVersionServlet"})
@MultipartConfig
public class CreateNewVersionServlet extends HttpServlet {

    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        User user = (User) request.getSession().getAttribute("user");

        int version = Integer.parseInt((String)request.getSession().getAttribute("version"));
        int exerciseID = Integer.parseInt((String)request.getSession().getAttribute("ex"));

        ArangoDBManager arangoDB = new ArangoDBManager();
        arangoDB.createNewVersion((version == 0) ? Integer.toString(exerciseID) : arangoDB.getKey(user.getUser_id(),exerciseID,version), arangoDB.getKey(user.getUser_id(),exerciseID,version+1));

        DbManager db = new DbManager();
        db.createNewVersion(version, version+1,user.getUser_id(),exerciseID);

        request.getSession().setAttribute("version", Integer.toString(version+1));
    }

    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
    }
}
