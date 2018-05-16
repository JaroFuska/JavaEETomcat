package main;

import dbmanager.DbManager;

import javax.servlet.RequestDispatcher;
import javax.servlet.ServletException;
import javax.servlet.annotation.MultipartConfig;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.Part;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;

@WebServlet(name = "/main.java.main.UploadServlet", urlPatterns = {"/main.java.main.UploadServlet"})
@MultipartConfig
public class UploadServlet extends HttpServlet {
    DbManager dbManager = new DbManager();

    // TODO: 22-Mar-18 there will be differences when this servlet will be called by student
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        int exercise = Integer.parseInt(request.getParameter("ex"));
        String text = request.getParameter("text");


        ArrayList<Part> parts = (ArrayList<Part>) request.getParts();
        for (Part part : parts) {
            if (part != null) {
                if (part.getName().equals("codes")) {
                    String path = part.getSubmittedFileName();
                    // TODO: 11-Apr-18 here i have all files submitted from form - upload them to DB, then download them from DB to code editor
                    dbManager.dp_exercise_files_file_insert(exercise, part.getInputStream(), path, text);
                }
            }
        }
        // TODO: 22-Mar-18 exercise.jsp has to be servlet to load content from exercise
        PrintWriter out = response.getWriter();
        out.println("Files were uploaded!");
        RequestDispatcher rd = request.getRequestDispatcher("exercise.jsp?ex=" + exercise);
        rd.forward(request,response);

    }

    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {

    }
}
