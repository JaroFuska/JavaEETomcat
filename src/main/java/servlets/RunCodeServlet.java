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
import java.io.*;

@WebServlet(name = "/main.java.servlets.RunCodeServlet", urlPatterns = {"/main.java.servlets.RunCodeServlet"})
@MultipartConfig
public class RunCodeServlet extends HttpServlet {


    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        User user = (User) request.getSession().getAttribute("user");
        String fileName = request.getParameter("fileName");
        String testType = request.getParameter("testType");
        String level = request.getParameter("level");
        Exercise exercise = (Exercise) request.getSession().getAttribute("exercise");
        String ret = "";
        String containerId = null;
        try {
            String imageName = user.getLogin().toLowerCase() + ":" + exercise.getId();
            String imageId = DockerManager.buildImage(fileName, imageName);
            containerId = DockerManager.createContainer(imageId);
            if (exercise.getType().toUpperCase().equals("TDD")) {
                if (testType.equals("test")) {
                    ExerciseFile userTest = exercise.getUserTest();
                    ret = DockerManager.execStart(containerId, String.format("%s %s", exercise.getLanguage(), userTest.getName()));
                } else if (testType.equals("master_test")) {
                    ExerciseFile masterTest = exercise.getMasterTest();
                    ret = DockerManager.execStart(containerId, String.format("%s %s Level%s", exercise.getLanguage(), masterTest.getName(), level));
                } else if (testType.equals("both")) {
                    ExerciseFile masterTest = exercise.getMasterTest();
                    ret = "Master tests:" +
                            "\n-----------------------------------------------------------";
                    ret += DockerManager.execStart(containerId, String.format("%s %s Level%s", exercise.getLanguage(), masterTest.getName(), level));
                    DockerManager.cleanUp(containerId);
                    containerId = DockerManager.createContainer(imageId);
                    ExerciseFile userTest = exercise.getUserTest();
                    ret += "User tests:" +
                            "\n-----------------------------------------------------------";
                    ret += DockerManager.execStart(containerId, String.format("%s %s", exercise.getLanguage(), userTest.getName()));
                }
            } else if (exercise.getType().toUpperCase().contains("LEGACY")) {

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
