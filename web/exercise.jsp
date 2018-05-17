<%@ page import="dbmanager.DbManager" %><%--
  Created by IntelliJ IDEA.
  User: jarof
  Date: 21-Mar-18
  Time: 20:36
  To change this template use File | Settings | File Templates.
--%>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%
    String ex = request.getParameter("ex");
    String userType = (String) request.getSession().getAttribute("userType");
    if (userType == null) {
        response.sendRedirect("index.jsp");
    }
    DbManager db = new DbManager();
    String exerciseDesc = db.getExerciseDesc(ex);
    boolean exerciseVis = db.getExerciseVisibility(ex);
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
    <li class="menu"><a href="logout.jsp">Logout</a></li>
</ul>
<h1>
    Exercise ${param.ex}
</h1>
<form method="post" action="main.java.main.UploadServlet" enctype="multipart/form-data">
    Exercise description: <textarea name="text" cols="100" rows="10"><%=exerciseDesc%></textarea><br>
    Directory with exercise files:<input type="file" name="codes" webkitdirectory mozdirectory msdirectory odirectory
                                         directory><br>
    Visible <input type="checkbox" id="visible" name="visible"><br>
    <input type="hidden" name="ex" value="<%=ex%>"/>
    <%--<input type="submit" value="Upload">--%>
    <button class="regular" type="submit">Upload</button>
</form>
<button class="regular" id="editCodes">Edit code</button>
<script type='text/javascript'>
    document.getElementById("visible").checked = <%=exerciseVis%>;
    document.getElementById("editCodes").addEventListener("click", function () {
        $.ajax({
            url: '/main.java.main.CreateProjectServlet',
            data: {
                ex: "<%=ex%>"
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

</script>
</body>
</html>
