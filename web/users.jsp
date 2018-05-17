<%@ page import="java.io.PrintWriter" %>
<%@ page import="dbmanager.DbManager" %>
<%@ page import="java.util.ArrayList" %>
<%@ page import="java.util.HashMap" %><%--
  Created by IntelliJ IDEA.
  User: jarof
  Date: 21-Mar-18
  Time: 23:34
  To change this template use File | Settings | File Templates.
--%>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%
    String userType = (String) request.getSession().getAttribute("userType");
    String userLogin = (String) request.getSession().getAttribute("userLogin");
    if (userType == null || userType == "student") {
        response.sendRedirect("index.jsp");
    }
    DbManager db = new DbManager();
    ArrayList<HashMap<String, String>> list = db.getAllUsers();
%>
<html>
<head>
    <link rel="stylesheet" type="text/css" href="/CSS/main.css">
    <title>Users</title>
</head>
<body>
<script src="jquery.fileTree/jquery.js" type="text/javascript"></script>
<ul class="menu">
    <li class="menu"><a href="exercises.jsp">Exercises</a></li>
    <li class="menu"><a href="settings.jsp">Settings</a></li>
    <li class="menu"><a href="users.jsp" class="active">Users</a></li>
    <li class="menu"><a href="logout.jsp">Logout</a></li>
</ul>
<table>
    <tr>
        <td>First name</td>
        <td>Last name</td>
        <td>Email</td>
        <td>Login</td>
        <td>Teacher</td>
        <td>Status</td>
    </tr>
    <%
        for (HashMap<String, String> map : list) {
            if ((map.get("teacher") == "false" && !userLogin.equals("admin")) || userLogin.equals("admin")) {
                String status;
                String buttonDisabled = map.get("status").equals("3") ? "false" : "true";
                switch (map.get("status")){
                    case "1": status = "Active"; break;
                    case "2": status = "Inactive"; break;
                    case "3": status = "Waiting for registration approval"; break;
                    default: status = "Unknown";
                }
    %>
        <tr>
            <td><%=map.get("first_name")%></td>
            <td><%=map.get("last_name")%></td>
            <td><%=map.get("email")%></td>
            <td><%=map.get("login")%></td>
            <td><%=map.get("teacher")%></td>
            <td><%=status%></td>
            <td><button class="smallButton" id="<%=map.get("user_id")%>">Approve registration</button></td>
            <script type="text/javascript">
                document.getElementById('<%=map.get("user_id")%>').disabled = ('<%=buttonDisabled%>' == 'true');
            </script>
    <%
    %>
        </tr>
            <%
                    }
        }
    %>
</table>
</body>
<script type="text/javascript">
    document.addEventListener('click', function (e) {
        e = e || window.event;
        var target = e.target || e.srcElement,
            id = target.id
        type = target.type;
        if (type == 'submit') {
                    $.ajax({
                        url: '/main.java.main.ApproveRegistration',
                        data: {
                            id: id
                        },
                        type: 'POST',
                        success: function (data) {
                            document.location.href = '/users.jsp';
                        }
                    });
                }

    }, false);
</script>
</html>
