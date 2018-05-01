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

@WebServlet(name = "/SaveCodeServlet", urlPatterns = {"/SaveCodeServlet"})
@MultipartConfig
public class SaveCodeServlet extends HttpServlet {
    DbManager dbManager = new DbManager();


    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        String fileName = request.getParameter("key");
        String content = request.getParameter("value");

        BufferedWriter writer = new BufferedWriter(new FileWriter(fileName));
        writer.write(content);

        writer.close();


//        String description = request.getParameter("description"); // Retrieves <input type="text" name="description">
//        Part filePart = request.getPart("file"); // Retrieves <input type="file" name="file">
//        String fileName = Paths.get(filePart.getSubmittedFileName()).getFileName().toString(); // MSIE fix.
//        InputStream fileContent = filePart.getInputStream();
//
////        File file = new File("/work/Catalina/localhost/ROOT", fileName);
//
////        Files.copy(fileContent, file.toPath());
//
//        dbManager.insert(fileName, fileName);
//
//
//
//        response.sendRedirect("tests.jsp");

    }

    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
    }
}
