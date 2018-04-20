<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<html lang="en">
<head>
    <meta charset="UTF-8">
    <meta http-equiv="X-UA-Compatible" content="IE=edge,chrome=1">
    <title>Editor</title>
    <style type="text/css" media="screen">
        body {
            overflow: hidden;
        }

        #editor {
            margin: 0;
            position: absolute;
            top: 0%;
            bottom: 0;
            left: 0;
            right: 0;
        }

        /*#someID {*/
        /*position: absolute;*/
        /*top: 0;*/
        /*width: 50%;*/
        /*height: 10%;*/
        /*left: 50%;*/
        /*}*/
        /*#printButt {*/
        /*position: absolute;*/
        /*top: 0;*/
        /*width: 50%;*/
        /*height: 10%;*/
        /*left: 0;*/
        /*}*/
    </style>
</head>
<body>


<%--<p id="someID"></p>--%>
<%--<form action="SaveCodeServlet" method="post" enctype="multipart/form-data">--%>
<%--<input type="submit" name="printButt" value="Button 1" />--%>
<%--<input type="file" name="file">--%>
<%--</form>--%>
<%--<input type="">--%>

<script>
    function printSomeStuff() {
        var editor = ace.edit("editor");
        var blob = new Blob([text], {type: "text/plain;charset=utf-8"});
        // saveAs(blob, filename+".txt");
        // document.getElementById("someID").innerText = editor.getAsFile();
    }
</script>


<pre id="editor" name="ed">
#Add python CODE here!

def func():
    for i in range(20):
        print(i)
</pre>

<script src="ace/ace.js" type="text/javascript" charset="utf-8"></script>
<script>
    var editor = ace.edit("editor");
    editor.setTheme("ace/theme/twilight");
    editor.session.setMode("ace/mode/python");
</script>


</body>
</html>
