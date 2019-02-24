package servlets;

import docker.DockerManager;
import main.User;

import javax.servlet.ServletException;
import javax.servlet.annotation.MultipartConfig;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.*;

@WebServlet(name = "/main.java.servlets.RunCodeServlet", urlPatterns = {"/main.java.servlets.RunCodeServlet"})
@MultipartConfig
public class RunCodeServlet extends HttpServlet {


    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        User user = (User) request.getSession().getAttribute("user");
        String fileName = request.getParameter("fileName");
        String level = request.getParameter("level");
        String ret = "";
        String containerId = null;
        try {
            String imageId = DockerManager.buildImage(fileName.substring(0, fileName.lastIndexOf("/")), user.getLogin().toLowerCase() + ":latest"); // TODO latest swap for exercise number
            containerId = DockerManager.createContainer(imageId);
            if (level == null) {
                ret = DockerManager.execStart(containerId, "python " + fileName.substring(fileName.lastIndexOf("/") + 1));
            } else {
//                TODO change tests to MasterTests
                ret = DockerManager.execStart(containerId, "python " + "tests.py TestStringMethods.test_level" + level);
            }

        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            DockerManager.cleanUp(containerId);
        }
        response.setContentType("text/plain");
        response.setCharacterEncoding("UTF-8");
        response.getWriter().write(ret);
    }

    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
    }
}
