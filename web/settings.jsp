<%--
  Created by IntelliJ IDEA.
  User: jarof
  Date: 13-May-18
  Time: 20:17
  To change this template use File | Settings | File Templates.
--%>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ page import="dbmanager.DbManager"%>
<%@ page import="java.util.HashMap" %>
<%
    String userType = (String) request.getSession().getAttribute("userType");
    String userLogin = (String) request.getSession().getAttribute("userLogin");
    if (userType == null) {
        response.sendRedirect("index.jsp");
    }
    DbManager db = new DbManager();
    HashMap<String, String> map = db.getUserData(userLogin);
%>
<html>
<head>
    <link rel="stylesheet" type="text/css" href="/CSS/main.css">
    <script src="jquery.fileTree/jquery.js" type="text/javascript"></script>
    <title>Update profile</title>
</head>
<body>
<ul class="menu">
    <li class="menu"><a href="exercises.jsp">Exercises</a></li>
    <li class="menu"><a href="settings.jsp" class="active">Settings</a></li>
    <li class="menu"><a href="logout.jsp">Logout</a></li>
</ul>
<%--TODO get user information from DB and fill fields--%>
<form action="RegistrationServlet" method="post" enctype="multipart/form-data">
    First name:<input type="text" name="firstname" value="<%=map.get("first_name")%>" required><br>
    Last name:<input type="text" name="lastname" value="<%=map.get("last_name")%>" required><br>
    Email:<input type="text" name="email" value="<%=map.get("email")%>" readonly><br>
    Login:<input type="text" name="login" value="<%=userLogin%>" readonly><br>
    Password:<input id="pass" type="password" name="password" onkeyup="passCheck()" required><br>
    Repeat password:<input id="repPass" type="password" name="repPassword" onkeyup="passCheck()" required><br>
    <button class="regular" id="submit" type="submit" disabled="true">Update</button>
</form>
</body>
<script type="text/javascript">
    document.getElementById("submit").disabled = true;
    function passCheck() {
        var pass = document.getElementById("pass").value;
        var pass2 = document.getElementById("repPass");
        if (pass != pass2.value) {
            pass2.style.color = "red";
            document.getElementById("submit").disabled = true;
        } else {
            pass2.style.color = "green";
            document.getElementById("submit").disabled = false;
        }
    }
</script>
</html>
