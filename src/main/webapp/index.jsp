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
        #register {
            background-color: #4978af;
        }
    </style>
    <title>Login page</title>
</head>
<body>
<script src="jquery.fileTree/jquery.js" type="text/javascript"></script>
<script type="text/javascript">
    function loginFunc() {
        var login = $("#login").val();
        var pass = $("#password").val();
        if (login != "" && pass != "") {
            $.ajax
            ({
                type: 'post',
                url: '/main.java.servlets.LoginServlet',
                data: {
                    login: login,
                    password: pass
                },
                success: function (response) {
                    if (response=='fail') {
                        //TODO - print message
                        alert("Wrong Details");
                    }
                    else {
                        window.location.href = "exercises.jsp";
                    }
                }
            });
        }
        else {
            alert("Please Fill All The Details");
        }
        return false;
    }
    function register() {
        window.location.href = "registration.jsp";
    }
</script>

<form method="post" action="LoginServlet" onsubmit="return loginFunc();">
    <div class="container">
        <label for="login"><b>Username</b></label>
        <input type="text" placeholder="Enter Username" id="login" name="login" required>

        <label for="password"><b>Password</b></label>
        <input type="password" placeholder="Enter Password" id="password" name="password" required>

        <button class="regular" type="submit">Login</button>
    </div>
</form>
<button class="regular" id="register" onclick="register()">Registration</button>

</body>
</html>
