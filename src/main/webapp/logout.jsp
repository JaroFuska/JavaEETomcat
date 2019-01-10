<%--
  Created by IntelliJ IDEA.
  User: jarof
  Date: 13-May-18
  Time: 20:08
  To change this template use File | Settings | File Templates.
--%>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%
    if(session != null)
        session.invalidate();
    request.getRequestDispatcher("/index.jsp").forward(request,response);
%>
<html>
<head>
</head>
<body>

</body>
</html>
