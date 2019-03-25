package main;

import java.io.*;
import java.util.ArrayList;

public class Parser {
    public static ArrayList<String> getMethodsWithDoc(String filename) {
        ArrayList<String> methods = new ArrayList<>();
        try {
            BufferedReader br = new BufferedReader(new FileReader(new File(filename)));
            String line = br.readLine();
            while (line != null) {
                String lineBefore = line;
                line = br.readLine();
                if (line != null && line.trim().startsWith("\"\"\"")) {
                    if (lineBefore.trim().startsWith("def")) {
                        methods.add(lineBefore.trim().substring(0, lineBefore.indexOf("(")).replace("def ", "").trim());
                    }
                }
            }
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return methods;
    }

    public static ArrayList<String> getFileMethods(String filename) {
        ArrayList<String> methods = new ArrayList<>();
        try {
            BufferedReader br = new BufferedReader(new FileReader(new File(filename)));
            String line = br.readLine();
            while (line != null) {
                if (line.trim().startsWith("def")) {
                    methods.add(line.trim().substring(0, line.indexOf("(")).replace("def ", "").trim());
                }
                line = br.readLine();
            }
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return methods;
    }
}
