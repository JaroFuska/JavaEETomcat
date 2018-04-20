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
    <title>Registration page</title>
</head>
<body>
<form action="RegistrationServlet" method="post" enctype="multipart/form-data">
    First name:<input type="text" name="firstname"><br>
    Last name:<input type="text" name="lastname"><br>
    Email:<input type="text" name="email"><br>
    Login:<input type="text" name="login"><br>
    Password:<input type="password" name="password"><br>
    Teacher <input type="checkbox" name="teacher"><br>
    <input type="submit" value="Register"><br>
<a href="index.jsp">Login</a>
</form>
</body>
</html>
