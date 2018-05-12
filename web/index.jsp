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
    <title>Login page</title>
</head>
<body>

<form action="LoginServlet" method="post" enctype="multipart/form-data">
<div class="container">
    <label for="login"><b>Username</b></label>
    <input type="text" placeholder="Enter Username" id="login" name="login" required>

    <label for="password"><b>Password</b></label>
    <input type="password" placeholder="Enter Password" id="password" name="password" required>

    <button type="submit">Login</button>
</div>
</form>

</body>
</html>
