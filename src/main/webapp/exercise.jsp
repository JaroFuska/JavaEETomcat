<%@ page import="dbmanager.DbManager" %>
<%@ page import="main.User" %><%--
  Created by IntelliJ IDEA.
  User: jarof
  Date: 21-Mar-18
  Time: 20:36
  To change this template use File | Settings | File Templates.
--%>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%
    String ex = request.getParameter("ex");
    request.getSession().setAttribute("ex", ex);
    User user = (User) request.getSession().getAttribute("user");
    boolean teacher = user.isTeacher();
    if (user == null) {
        response.sendRedirect("index.jsp");
    }
    DbManager db = new DbManager();
    boolean newExercise = (Integer.parseInt(ex) > db.getExercisesCount());
    String exerciseDesc = (newExercise) ? "" : db.getExerciseDesc(ex);
    boolean exerciseVis = (newExercise) ? false : db.getExerciseVisibility(ex);
%>
<html>
<script src="jquery.fileTree/jquery.js" type="text/javascript"></script>
<link rel="stylesheet" type="text/css" href="/CSS/main.css">
<head>
    <title>Exercise preparation</title>
</head>
<body>
<ul class="menu">
    <li class="menu"><a href="exercises.jsp">Exercises</a></li>
    <li class="menu"><a href="settings.jsp">Settings</a></li>
    <li id="users"><a href="users.jsp">Users</a></li>
    <li class="menu"><a href="logout.jsp">Logout</a></li>
</ul>
<h1>
    Exercise ${param.ex}
</h1>
<form method="post" action="main.java.servlets.UploadServlet" enctype="multipart/form-data">
    Exercise description: <textarea name="text" cols="100" rows="10"><%=exerciseDesc%></textarea><br>
    Directory with exercise files:<input type="file" name="codes" webkitdirectory mozdirectory msdirectory odirectory
                                         directory><br>
    Visible <input type="checkbox" id="visible" name="visible"><br>
    <input type="hidden" name="ex" value="<%=ex%>"/>
    <button class="regular" type="submit">Upload</button>
</form>
<button class="regular" id="editCodes">Edit code</button>
<script type='text/javascript'>
    document.getElementById("visible").checked = <%=exerciseVis%>;
    document.getElementById("editCodes").addEventListener("click", function () {
        $.ajax({
            url: '/main.java.servlets.CreateProjectServlet',
            data: {
                ex: "<%=ex%>",
                versionChange: 'false'
            },
            type: 'POST',
            success: function (data) {
                if (data == "") {
                    alert("There is no code yet!");
                } else {
                    document.location.href = '/editCode.jsp?root=' + data;
                }
            }
        });
    });

    if (!<%=teacher%>) {
        document.getElementById("users").className = "hide";
    } else {
        document.getElementById("users").className = "menu";
    }
</script>
</body>
</html>
