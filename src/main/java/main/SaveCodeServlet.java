package main;

import dbmanager.ArangoDBManager;
import dbmanager.DbManager;

import javax.servlet.ServletException;
import javax.servlet.annotation.MultipartConfig;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.Part;
import java.io.*;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

@WebServlet(name = "/main.java.main.SaveCodeServlet", urlPatterns = {"/main.java.main.SaveCodeServlet"})
@MultipartConfig
public class SaveCodeServlet extends HttpServlet {

    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        String fileName = request.getParameter("key");
        String content = request.getParameter("value");

        User user = (User) request.getSession().getAttribute("user");

        File file = new File(fileName);

        BufferedWriter writer = new BufferedWriter(new FileWriter(fileName));
        writer.write(content);

        writer.close();
        int version = Integer.parseInt((String)request.getSession().getAttribute("version"));
        int exerciseID = Integer.parseInt((String)request.getSession().getAttribute("ex"));

        ArangoDBManager arangoDB = new ArangoDBManager();
        String key = arangoDB.getKey(user.getUser_id(), exerciseID, version);
        arangoDB.updateAttribute(key, ((fileName.startsWith("/")) ? fileName.substring(1) : fileName), content);


//        DbManager db = new DbManager();
//        db.dp_user_files_update(user.getUser_id(), Integer.parseInt((String) request.getSession().getAttribute("ex")),
//                new FileInputStream(file), fileName, version);

    }

    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
    }
}
