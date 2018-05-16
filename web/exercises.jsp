<%@ page import="java.io.PrintWriter" %><%--
  Created by IntelliJ IDEA.
  User: jarof
  Date: 21-Mar-18
  Time: 23:34
  To change this template use File | Settings | File Templates.
--%>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%
    String userType = (String) request.getSession().getAttribute("userType");
    if (userType == null) {
        response.sendRedirect("index.jsp");
    }
%>
<html>
<head>
    <link rel="stylesheet" type="text/css" href="/CSS/main.css">
    <title>Exercises</title>
</head>
<body>
<script src="jquery.fileTree/jquery.js" type="text/javascript"></script>
<ul class="menu">
    <li class="menu"><a href="exercises.jsp" class="active">Exercises</a></li>
    <li class="menu"><a href="settings.jsp">Settings</a></li>
    <li class="menu"><a href="logout.jsp">Logout</a></li>
</ul>
</body>
<script type="text/javascript">
    var ex;
    $.post('/main.java.main.ExercisesServlet', function (data) {
        ex = data;
        var i;
        for (i = 1; i <= data; i++) {
            var btn = document.createElement("BUTTON");
            btn.id = i;
            btn.className = "regular";
            var t = document.createTextNode("Exercise " + i);
            btn.appendChild(t);
            document.getElementById("exercisesHolder").appendChild(btn);
        }
    });

    document.addEventListener('click', function (e) {
        e = e || window.event;
        var target = e.target || e.srcElement,
            id = target.id
        type = target.type;
        if (type == 'submit') {
            if (id == "addExercise") {
                window.location.href = "exercise.jsp?ex=" + (parseInt(ex) + 1).toString();
            } else {
                if ('<%=userType%>' == 'student') {
                    $.ajax({
                        url: '/main.java.main.CreateProjectServlet',
                        data: {
                            ex: id
                        },
                        type: 'POST',
                        success: function (data) {
                            document.location.href = '/editCode.jsp?root=' + data;
                        }
                    });
                } else {
                    window.location.href = "exercise.jsp?ex=" + id;
                }
            }
        }
    }, false);
</script>
<div id="exercisesHolder"></div>
<button class="regular" id="addExercise">Add exercise</button>
<script>
    if ('<%=userType%>' == 'student') {
        document.getElementById('addExercise').style.visibility = 'hidden';
    } else {
        document.getElementById('addExercise').style.visibility = 'visible';
    }
</script>
</html>
