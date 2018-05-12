<%--
  Created by IntelliJ IDEA.
  User: jarof
  Date: 21-Mar-18
  Time: 20:36
  To change this template use File | Settings | File Templates.
--%>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%
    String ex = request.getParameter("ex");
    String user = (String) request.getSession().getAttribute("user");
%>
<html>
<script src="jquery.fileTree/jquery.js" type="text/javascript"></script>
<script type='text/javascript'>
    alert('<%=user%>');
    if ('<%=user%>' == 'student') {
        alert("nehehe")
        <%--document.getElementById('editCode').value = "<%=ex%>";--%>
        $.ajax({
            url: '/CreateProjectServlet',
            data: {
                ex: "<%=ex%>"
            },
            type: 'POST',
            success: function (data) {
                document.location.href = '/jquerytree.jsp?root=' + data;
            }
        });
    }
</script>
<head>
    <title>Exercise preparation</title>
</head>
<body>
<h1>
    Exercise ${param.ex}
</h1>
<form method="post" action="UploadServlet" enctype="multipart/form-data">
    Exercise description: <textarea name="text" cols="100" rows="10"></textarea><br>
    Directory with exercise files:<input type="file" name="codes" webkitdirectory mozdirectory msdirectory odirectory
                                         directory><br>
    <input type="hidden" name="ex" value="<%=ex%>"/>
    <input type="submit" value="Upload">
</form>

<form method="post" action="CreateProjectServlet" enctype="multipart/form-data">
    <%--TODO add version of exercise to edit--%>
    <input type="hidden" id="editCode" name="ex" value="<%=ex%>"/>
    <input type="submit" value="Edit code">
</form>
</body>
</html>
