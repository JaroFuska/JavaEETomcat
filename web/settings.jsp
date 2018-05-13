<%--
  Created by IntelliJ IDEA.
  User: jarof
  Date: 13-May-18
  Time: 20:17
  To change this template use File | Settings | File Templates.
--%>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%
    String userType = (String) request.getSession().getAttribute("userType");
    String userLogin = (String) request.getSession().getAttribute("userLogin");
    if (userType == null) {
        response.sendRedirect("index.jsp");
    }
%>
<html>
<head>
    <link rel="stylesheet" type="text/css" href="/CSS/main.css">
    <title>Update profile</title>
</head>
<body>
<%--TODO get user information from DB and fill fields--%>
<form action="RegistrationServlet" method="post" enctype="multipart/form-data">
    First name:<input type="text" name="firstname" required><br>
    Last name:<input type="text" name="lastname" required><br>
    Email:<input type="text" name="email" required><br>
    Login:<input type="text" name="login"><br>
    Password:<input id="pass" type="password" name="password" onkeyup="passCheck()" required><br>
    Repeat password:<input id="repPass" type="password" name="repPassword" onkeyup="passCheck()" required><br>
    Teacher <input type="checkbox" name="teacher"><br>
    <button type="submit">Update</button>
</form>
</body>
<script>
    function passCheck() {
        var pass = document.getElementById("pass").value;
        var pass2 = document.getElementById("repPass");
        if (pass != pass2.value) {
            pass2.style.color = "red";
        } else {
            pass2.style.color = "green";
        }
    }
</script>
</html>
