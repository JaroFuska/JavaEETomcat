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
import javax.swing.*;
import java.awt.*;
import java.io.IOException;
import java.util.ArrayList;

import static javax.swing.JOptionPane.QUESTION_MESSAGE;

@WebServlet(name = "/main.java.servlets.MethodsSelectorServlet", urlPatterns = {"/main.java.servlets.MethodsSelectorServlet"})
@MultipartConfig
public class MethodsSelectorServlet extends HttpServlet {


    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        String fileName = request.getParameter("fileName");
        String level = request.getParameter("level");
        Exercise exercise = (Exercise) request.getSession().getAttribute("exercise");
        String ret = "";
        ArrayList<String> allMethods = new ArrayList<>();
        ArrayList<String> methodsFromXML = new ArrayList<>();
        try {
            JPanel methodsPanel = new JPanel();
            methodsPanel.setLayout(new GridLayout(0, 1));
            if (exercise.getLanguage().equals("python")) {
                for (ExerciseFile ef : exercise.getFiles().values()) {
                    if (ef.isCode()) {
                        for (String method : Parser.getFileMethods(fileName + "/" + ef.getName())) {
                            JCheckBox box = new JCheckBox(method);
                            methodsPanel.add(box);
                            allMethods.add(method);
                        }
                    }
                }
            }
            for (LevelMethod lm : exercise.getLevel(level).getMethods()) {
                if (lm.getType().equals("1")) {
                    methodsFromXML.add(lm.getName());
                }
            }
            UIManager.put("OptionPane.minimumSize", new Dimension(500, 300));
            JFrame jf = new JFrame();
            jf.setAlwaysOnTop(true);
            JOptionPane.showMessageDialog(jf, methodsPanel, "Select methods that will be affected by change", QUESTION_MESSAGE);
            int mismatchedMethods = 0;
            int missingMethods = 0;
            for (Component b : methodsPanel.getComponents()) {
                JCheckBox cb = (JCheckBox) b;
                if (cb.isSelected()) {
                    if (!methodsFromXML.contains(cb.getText())) {
                        mismatchedMethods++;
                    }
                } else {
                    if (methodsFromXML.contains(cb.getText())) {
                        missingMethods++;
                    }
                }
            }
            if (mismatchedMethods > 0) {
                ret = mismatchedMethods + " of methods that you selected are not affected by change";
            }
            if (missingMethods > 0) {
                ret += "\nYou missed " + missingMethods + " methods";
            }


        } catch (Exception e) {
            e.printStackTrace();
        }
        response.setContentType("text/plain");
        response.setCharacterEncoding("UTF-8");
        response.getWriter().write(ret.trim());
    }

    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
    }
}
