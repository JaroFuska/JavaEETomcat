package servlets;

import docker.DockerManager;
import main.User;
import main.XMLClasses.Exercise;
import main.XMLClasses.ExerciseFile;

import javax.servlet.ServletException;
import javax.servlet.annotation.MultipartConfig;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

@WebServlet(name = "/main.java.servlets.RunCodeServlet", urlPatterns = {"/main.java.servlets.RunCodeServlet"})
@MultipartConfig
public class RunCodeServlet extends HttpServlet {


    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        User user = (User) request.getSession().getAttribute("user");
        String fileName = request.getParameter("fileName");
        String  currFileName = fileName.substring(fileName.lastIndexOf("/")+1, fileName.length());
        fileName = fileName.substring(0, fileName.lastIndexOf("/"));
        Exercise exercise = (Exercise) request.getSession().getAttribute("exercise");
        String ret = "";
        String containerId = null;
        try {
            String imageName = user.getLogin().toLowerCase() + ":" + exercise.getId();
            String imageId = DockerManager.buildImage(fileName, imageName);
            containerId = DockerManager.createContainer(imageId);
            if (exercise.getLanguage().equals("python")) {
                ret = DockerManager.execStart(containerId, String.format("%s %s", exercise.getLanguage(), currFileName));
            } else if (exercise.getLanguage().equals("C++")) {
                ret = DockerManager.execStart(containerId, String.format("./code"));
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

    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws
            ServletException, IOException {
    }
}
