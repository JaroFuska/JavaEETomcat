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
    <title>Login page</title>
</head>
<body>
<form action="LoginServlet" method="post" enctype="multipart/form-data">
    Login:<input id="login" type="text" name="login"><br>
    Password:<input id="password" type="password" name="password"><br>
    <input type="submit" value="Login"><br>
<a href="registration.jsp">Register</a>
</form>
</body>
</html>
