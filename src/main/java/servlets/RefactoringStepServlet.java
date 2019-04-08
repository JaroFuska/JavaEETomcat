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

@WebServlet(name = "/main.java.servlets.RefactoringStepServlet", urlPatterns = {"/main.java.servlets.RefactoringStepServlet"})
@MultipartConfig
public class RefactoringStepServlet extends HttpServlet {


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
            if (exercise.getType().toUpperCase().contains("REFACTOR")) {
                ExerciseFile userTest = exercise.getUserTest();
                ExerciseFile masterTest = exercise.getMasterTest();
                ret = "User tests:" +
                        "\n-----------------------------------------------------------\n";
                ret += DockerManager.execStart(containerId, String.format("%s %s", exercise.getLanguage(), userTest.getName()));
                DockerManager.cleanUp(containerId);
                containerId = DockerManager.createContainer(imageId);
                ret += "\n\n***********************************************************" +
                        "\nMaster tests:" +
                        "\n-----------------------------------------------------------\n";
                ret += DockerManager.execStart(containerId, String.format("%s %s Level%s.test_step%s", exercise.getLanguage(), masterTest.getName(), level, step));
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
