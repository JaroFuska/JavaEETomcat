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
import java.util.ArrayList;
import java.util.Arrays;
import java.util.StringJoiner;
import java.util.stream.Collectors;

@WebServlet(name = "/main.java.servlets.CheckDocServlet", urlPatterns = {"/main.java.servlets.CheckDocServlet"})
@MultipartConfig
public class CheckDocServlet extends HttpServlet {


    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        User user = (User) request.getSession().getAttribute("user");
        String fileName = request.getParameter("fileName");
        String step = request.getParameter("step");
        String level = request.getParameter("level");
        Exercise exercise = (Exercise) request.getSession().getAttribute("exercise");
        String doc = "";
        String ret = "";
        String containerId = null;
        try {
            String imageName = user.getLogin().toLowerCase() + ":" + exercise.getId();
            String imageId = DockerManager.buildImage(fileName, imageName);
            containerId = DockerManager.createContainer(imageId);
            if (exercise.getLanguage().equals("python")) {
                if (step.equals("1") || step.equals("5")) {
                    StringJoiner exFiles = new StringJoiner(" ");
                    for (ExerciseFile ef : exercise.getFiles().values()) {
                        if (ef.isCode())
                            exFiles.add(ef.getNameWithoutFileType());
                    }
                    doc = DockerManager.execStart(containerId, String.format("%s -m pydoc %s", exercise.getLanguage(), exFiles.toString()));
                }
            }
            ret = parseDoc(doc, level, step, exercise);


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


    private String parseDoc(String doc, String level, String step, Exercise exercise) {
        String ret = "";
        doc = doc.replaceAll(".\b", "").replaceAll("\r", "");
        ArrayList<String> functionsList = new ArrayList(Arrays.asList(doc.split("FUNCTIONS")));
        functionsList.remove(0);
        String functions = "";
        int index = 0;
        for (String f : functionsList) {
            functionsList.set(index, f.substring(0, f.indexOf("FILE\n")));
            functions += functionsList.get(index).trim().replaceAll("    ", "") + "\n\n";
            index++;
        }
        functionsList = new ArrayList<>(Arrays.asList(functions.split("\n\n")));
        ArrayList<String> methodsWithDoc = (ArrayList) functionsList.stream().filter(s -> s.split("\n").length > 1).map(s -> s.substring(0, s.indexOf("("))).collect(Collectors.toList());
        if (step.equals("1") || step.equals("5")){
            for (LevelMethod lm : exercise.getLevel(level).getMethods()) {
                if (lm.getType().equals("1") && step.equals("1") && !methodsWithDoc.contains(lm.getName())) {
                    ret += String.format("Missing documentation for method '%s'\n", lm.getName());
                }
                if (lm.getType().equals("2") && step.equals("5") && !methodsWithDoc.contains(lm.getName())) {
                    ret += String.format("Missing documentation for method '%s'\n", lm.getName());
                }
            }
        }
        return ret.equals("") ? "OK" : ret;
    }
}
