import main.XMLClasses.Exercise;
import main.XMLClasses.LevelMethod;

import javax.swing.*;
import java.awt.*;
import java.io.*;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.StringJoiner;
import java.util.stream.Collectors;

import static javax.swing.JOptionPane.QUESTION_MESSAGE;

public class TestClass {

    public static void main(String[] args) {
        JPanel methodsPanel = new JPanel();
        methodsPanel.setLayout(new GridLayout(0, 1));
        for (int i = 0; i < 4; i++) {
            JCheckBox box = new JCheckBox(i + "");
            methodsPanel.add(box);
        }
        UIManager.put("OptionPane.minimumSize",new Dimension(500,300));
        JOptionPane.showMessageDialog(null, methodsPanel, "Select methods that will be affected by change", QUESTION_MESSAGE);
        for (Component b : methodsPanel.getComponents()) {
            JCheckBox cb = (JCheckBox) b;
            if (cb.isSelected()) {
                System.out.println(cb.getText());
            }
        }
    }

    private static String parseDoc(String doc, String level, String step, Exercise exercise) {
        String ret = "";
        doc = doc.replaceAll(".\b", "");
        ArrayList<String> functionsList = new ArrayList(Arrays.asList(doc.split("FUNCTIONS")));
        functionsList.remove(0);
        String functions = "";
        int index = 0;
        for (String f : functionsList) {
            functionsList.set(index, f.substring(0, f.lastIndexOf("FILE")));
            functions += functionsList.get(index).trim().replaceAll("    ", "");
            index++;
        }
        functionsList = new ArrayList<>(Arrays.asList(functions.split("\n\n")));
        ArrayList<String> methodsWithDoc = (ArrayList) functionsList.stream().filter(s -> s.split("\n").length > 1).collect(Collectors.toList());
        if (step.equals("1")) {
            for (LevelMethod lm : exercise.getLevels().get(level).getMethods()) {
                if (lm.getType().equals("1") && !methodsWithDoc.contains(lm.getName())) {
                    ret += String.format("Missing documentation for method '%s'\n", lm.getName());
                }
            }
        }
        return ret.equals("") ? "OK" : ret;
    }

    private static ArrayList<String> getMethodsWithDoc(String filename) {
        ArrayList<String> methods = new ArrayList<>();
        return methods;
    }
}
