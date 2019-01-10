package main;

import com.spotify.docker.client.DockerClient;
import docker.DockerManager;

import javax.servlet.ServletException;
import javax.servlet.annotation.MultipartConfig;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.*;

@WebServlet(name = "/main.java.main.RunCodeServlet", urlPatterns = {"/main.java.main.RunCodeServlet"})
@MultipartConfig
public class RunCodeServlet extends HttpServlet {


    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        String fileName = request.getParameter("fileName");
        String s;
        String ret = "";
        try {
//            Process p = Runtime.getRuntime().exec("python \"" + fileName + "\"");
//
//            BufferedReader stdInput = new BufferedReader(new
//                    InputStreamReader(p.getInputStream()));
//
//            BufferedReader stdError = new BufferedReader(new
//                    InputStreamReader(p.getErrorStream()));
//
//            // read the output from the command
//            while ((s = stdInput.readLine()) != null) {
//                System.out.println(s);
//                ret += "\n" + s;
//            }
//
//            // read any errors from the attempted command
//            while ((s = stdError.readLine()) != null) {
//                System.out.println(s);
//                ret += "\n" + s;
//            }


//            DockerManager.buildImage(filename)
            String imageId = DockerManager.buildImage(fileName.substring(0, fileName.lastIndexOf("/")), "testingimage:latest");
            String containerId = DockerManager.createContainer(imageId);
            ret = DockerManager.execStart(containerId, "python -m unittest discover");

        } catch (Exception e) {
            e.printStackTrace();
        }
        response.setContentType("text/plain");
        response.setCharacterEncoding("UTF-8");
        response.getWriter().write(ret);
    }

    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
    }
}
