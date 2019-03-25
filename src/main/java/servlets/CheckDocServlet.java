package servlets;

import main.Parser;
import main.XMLClasses.Exercise;
import main.XMLClasses.ExerciseFile;
import main.XMLClasses.LevelMethod;

import javax.servlet.ServletException;
import javax.servlet.annotation.MultipartConfig;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.*;
import java.util.ArrayList;

@WebServlet(name = "/main.java.servlets.CheckDocServlet", urlPatterns = {"/main.java.servlets.CheckDocServlet"})
@MultipartConfig
public class CheckDocServlet extends HttpServlet {


    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        String fileName = request.getParameter("fileName");
        String step = request.getParameter("step");
        String level = request.getParameter("level");
        Exercise exercise = (Exercise) request.getSession().getAttribute("exercise");
        String ret = "";
        ArrayList<String> methodsWithDoc = new ArrayList<>();
        try {
            if (exercise.getLanguage().equals("python")) {
                for (ExerciseFile ef : exercise.getFiles().values()) {
                    if (ef.isCode())
                        methodsWithDoc.addAll(Parser.getMethodsWithDoc(fileName + "/" + ef.getName()));
                }
            }
            for (LevelMethod lm : exercise.getLevel(level).getMethods()) {
                if (step.equals("1") && lm.getType().equals("1") && !methodsWithDoc.contains(lm.getName())) {
                    ret += "Missing documentation for method " + lm.getName() + "\n";
                }
                if (step.equals("5") && lm.getType().equals("2") && !methodsWithDoc.contains(lm.getName())) {
                    ret += "Missing documentation for method " + lm.getName() + "\n";
                }
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
        response.setContentType("text/plain");
        response.setCharacterEncoding("UTF-8");
        response.getWriter().write(ret.equals("") ? "OK" : ret);

    }

    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
    }

}
