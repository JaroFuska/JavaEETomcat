package servlets;

import docker.DockerManager;
import main.User;
import main.XMLClasses.Exercise;
import main.XMLClasses.ExerciseFile;
import main.XMLClasses.LevelMethod;

import javax.servlet.ServletException;
import javax.servlet.annotation.MultipartConfig;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.StringJoiner;

@WebServlet(name = "/main.java.servlets.RunCodeServletLegacy", urlPatterns = {"/main.java.servlets.RunCodeServletLegacy"})
@MultipartConfig
public class RunCodeServletLegacy extends HttpServlet {


    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        User user = (User) request.getSession().getAttribute("user");
        String fileName = request.getParameter("fileName");
        String step = request.getParameter("step");
        String level = request.getParameter("level");
        Exercise exercise = (Exercise) request.getSession().getAttribute("exercise");
        String ret = "";
        String containerId = null;
        try {
            String imageName = user.getLogin().toLowerCase() + ":" + exercise.getId();
            String imageId = DockerManager.buildImage(fileName, imageName);
            containerId = DockerManager.createContainer(imageId);
            if (exercise.getType().toUpperCase().contains("LEGACY")) {
                if (step.equals("2") || step.equals("6")) {
                    ExerciseFile userTest = exercise.getUserTest();
                    StringJoiner methodTests = new StringJoiner(" ");
                    for (LevelMethod lm : exercise.getLevel(level).getMethods()) {
                        if (lm.getType().equals("1") && step.equals("2")) {
                            methodTests.add("UserTests.test_" + lm.getName());
                        } else if (lm.getType().equals("2") && step.equals("6")) {
                            methodTests.add("UserTests.test_" + lm.getName());
                        }
                    }
                    ret = DockerManager.execStart(containerId, String.format("%s %s %s", exercise.getLanguage(), userTest.getName(), methodTests.toString()));
                    if (!ret.contains("FAIL")) {
                        String[] split = ret.split("\n");
                        ret = split[split.length - 1];
                    }
                }
                if (step.equals("3")) {
                    ExerciseFile masterTest = exercise.getMasterTest();
                    ret = DockerManager.execStart(containerId, String.format("%s %s Level%s", exercise.getLanguage(), masterTest.getName(), level));
                    if (!ret.contains("FAIL")) {
                        ret = "OK";
                    }
                }
                if (step.equals("4")) {
                    ExerciseFile masterTest = exercise.getMasterTest();
                    ret = "Master tests:" +
                            "\n-----------------------------------------------------------";
                    ret += DockerManager.execStart(containerId, String.format("%s %s Level%s", exercise.getLanguage(), masterTest.getName(), level));
                    DockerManager.cleanUp(containerId);
                    containerId = DockerManager.createContainer(imageId);
                    ExerciseFile userTest = exercise.getUserTest();
                    ret += "\n\n\n\n\nUser tests:" +
                            "\n-----------------------------------------------------------\n";
                    ret += DockerManager.execStart(containerId, String.format("%s %s", exercise.getLanguage(), userTest.getName()));
                }
            }

        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            DockerManager.cleanUp(containerId);
        }
        response.setContentType("text/plain");
        response.setCharacterEncoding("UTF-8");
        response.getWriter().write(ret.trim());
    }

    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
    }
}
