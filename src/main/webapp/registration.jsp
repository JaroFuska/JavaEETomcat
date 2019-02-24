<%--
  Created by IntelliJ IDEA.
  User: jarof
  Date: 28-Feb-18
  Time: 14:50
  To change this template use File | Settings | File Templates.
--%>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<html>
<head>
    <link rel="stylesheet" type="text/css" href="/CSS/main.css">
    <style type="text/css" media="screen">
        #login {
            background-color: #4978af;
        }
    </style>
    <title>Registration page</title>
</head>
<body>
<script type="text/javascript">
    function login() {
        window.location.href = "index.jsp";
    }
</script>
<form action="/main.java.servlets.RegistrationServlet" method="post" enctype="multipart/form-data">
    First name:<input type="text" name="firstname" required><br>
    Last name:<input type="text" name="lastname" required><br>
    Email:<input type="text" name="email" required><br>
    Login:<input type="text" name="login"><br>
    Password:<input id="pass" type="password" name="password" onkeyup="passCheck()" required><br>
    Repeat password:<input id="repPass" type="password" name="repPassword" onkeyup="passCheck()" required><br>
    Teacher:<input id="teacher" type="checkbox" name="teacher"><br>
    <button class="regular" id="submit" type="submit" disabled="true">Register</button>
</form>
<button class="regular" id="login" onclick="login()">Login</button>
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
