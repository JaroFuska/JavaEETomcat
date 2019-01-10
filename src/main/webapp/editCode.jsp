<%@ page import="java.io.File" %>
<%@ page import="dbmanager.DbManager" %>
<%@ page import="main.User" %>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%
    String root = request.getParameter("root");
    User user = (User) request.getSession().getAttribute("user");
    if (user == null) {
        response.sendRedirect("index.jsp");
    }
    boolean teacher = user.isTeacher();
    String ex = (String) request.getSession().getAttribute("ex");
    DbManager db = new DbManager();
    String exerciseDesc = db.getExerciseDesc(ex);
    int version = Integer.parseInt((String) request.getSession().getAttribute("version"));
%>
<html lang="en">
<head>
    <title>Code editor</title>
    <link rel="stylesheet" type="text/css" href="/CSS/main.css">
    <meta http-equiv="Content-Type" content="text/html;charset=utf-8"/>

    <style type="text/css">
        BODY,
        HTML {
            padding: 0px;
            margin: 0px;
            height: 100%;
            width: 100%;
        }

        BODY {
            font-family: Verdana, Arial, Helvetica, sans-serif;
            font-size: 11px;
            background: #EEE;
            padding: 5px;
            height: 100%;
            width: 100%;
        }

        H1 {
            font-family: Georgia, serif;
            font-size: 20px;
            font-weight: normal;
        }

        H2 {
            font-family: Georgia, serif;
            font-size: 16px;
            font-weight: normal;
            margin: 0px 0px 10px 0px;
        }

        .containerC {
            height: 92%;
            width: 100%;
            margin: auto;
            padding: 5px;
        }

        .projectStructureC {
            width: 20%;
            height: 100%;
            border-top: solid 1px #BBB;
            border-left: solid 1px #BBB;
            border-bottom: solid 1px #000000;
            border-right: solid 1px #000000;
            background: #2b2b2b;
            overflow: scroll;
            padding: 5px;
            font-family: Georgia, serif;
            font-size: 20px;
            font-weight: normal;
            color: whitesmoke;
            float: left;
        }

        #editor {
            margin-left: 20%;
            height: 100%;
            overflow: scroll;
            padding: 5px;
            margin-top: 0;
            border-top: solid 1px #BBB;
            border-left: solid 1px #BBB;
            border-bottom: solid 1px #000000;
            border-right: solid 1px #000000;
        }

        #uploadFiles {
            height: 5%;
            float: bottom;
        }

        #runFiles {
            height: 5%;
            float: bottom;
        }

        #createNewProjectVersion {
            height: 5%;
            float: bottom;
        }

        #ctxMenu {
            display: none;
            z-index: 100;
        }

        menu {
            position: absolute;
            display: block;
            left: 0px;
            top: 0px;
            height: 20px;
            width: 20px;
            padding: 0;
            margin: 0;
            border: 1px solid;
            background-color: white;
            font-weight: normal;
            white-space: nowrap;
        }

        menu:hover {
            background-color: #eef;
            font-weight: bold;
        }

        menu:hover > menu {
            display: block;
        }

        menu > menu {
            display: none;
            position: relative;
            top: -20px;
            left: 100%;
            width: 100px;
        }

        menu[title]:before {
            content: attr(title);
        }

        menu:not([title]):before {
            content: "\2630";
        }

    </style>

    <script src="jquery.fileTree/jquery.js" type="text/javascript"></script>
    <script src="jquery.fileTree/jquery.easing.js" type="text/javascript"></script>
    <script src="jquery.fileTree/jqueryFileTree.js" type="text/javascript"></script>
    <script src="ace/ace.js" type="text/javascript" charset="utf-8"></script>
    <link href="jquery.fileTree/jqueryFileTree.css" rel="stylesheet" type="text/css" media="screen"/>

    <script type="text/javascript">
        var editor;
        var editedFiles = new Map();
        var currentFile = null;
        $(window).load(function () {
            editor = ace.edit("editor");
            editor.setTheme("ace/theme/twilight");
            editor.session.setMode("ace/mode/python");
            editor.commands.addCommand({
                name: 'save',
                bindKey: {win: "Ctrl-S", "mac": "Cmd-S"},
                exec: function (editor) {
                    // TODO handle save - call upload file for just currentFile
                    // alert(editor.session.getValue());
                }
            });
            editor.on("change", function () {
                editedFiles.set(currentFile, editor.session.getValue());
                // alert(editedFiles.size);
            });

            var projectStructure = document.getElementById("projectStructure");
            projectStructure.addEventListener("contextmenu", function (event) {
                event.preventDefault();
                var ctxMenu = document.getElementById("ctxMenu");
                ctxMenu.style.display = "block";
                ctxMenu.style.left = (event.pageX - 10) + "px";
                ctxMenu.style.top = (event.pageY - 10) + "px";
                var addFile = document.getElementById("addFile");
                addFile.addEventListener("click", function (event) {
                    alert("Creating new file");
                });
            }, false);
            projectStructure.addEventListener("click", function (event) {
                var ctxMenu = document.getElementById("ctxMenu");
                ctxMenu.style.display = "";
                ctxMenu.style.left = "";
                ctxMenu.style.top = "";
            }, false);


        })


        $(document).ready(function () {
            $('#projectStructure').fileTree({
                root: '<%=root%>',
                script: 'jqueryFileTree.jsp',
                folderEvent: 'click',
                expandSpeed: 750,
                collapseSpeed: 750,
                expandEasing: 'easeOutBounce',
                collapseEasing: 'easeInBack',
                loadMessage: 'Please wait...',
                folderEvent: 'dblclick'
            }, function (file) {
                currentFile = file;
                <%-- TODO save file to map only if it changed and opening a new file --%>
                // editedFiles.set(file, editor.session.getValue());
                if (editedFiles.has(file)) {
                    editor.session.setValue(editedFiles.get(file), -1);
                } else {
                    document.getElementById("fileName").value = file;
                    $.post('/main.java.main.SetEditorTextServlet?fileName=' + file, function (data) {
                        editor.session.setValue(data, -1);
                    });
                }
            });
        });


        function uploadFiles() {
            for (let [key, value] of editedFiles) {
                $.ajax({
                    url: '/main.java.main.SaveCodeServlet',
                    data: {
                        key: key,
                        value: value
                    },
                    type: 'POST'
                });
            }
            editedFiles.clear();
        }

        function createNewVersion() {
            uploadFiles();
            $.post('/main.java.main.CreateNewVersionServlet', function (data) {

            });
        }

        function runFiles() {
            uploadFiles();
            //TODO - find main method and run that file
            $.post('/main.java.main.RunCodeServlet?fileName=' + currentFile, function (data) {
                alert(data);
            });
        }

        function showDesc() {
            alert('<%=exerciseDesc%>');
        }

        // $(".menu").click(function(){
        //     var href = $(this).attr("href");
        //     uploadFiles();
        //     window.location.href = href;
        // });

        window.onbeforeunload = function () {
            if (editedFiles.size > 0) {
                uploadFiles();
                return 'Save your code first';
            } else {
                return;
            }
        };

        // $(window).bind('beforeunload', function(){
        //     uploadFiles();
        //     return 'Are you sure you want to leave?';
        // });

        // $(window).unload(function(){
        //     uploadFiles();
        // });

    </script>


</head>

<body>
<ul class="menu">
    <li class="menu"><a href="exercises.jsp">Exercises</a></li>
    <li class="menu"><a href="settings.jsp">Settings</a></li>
    <li class="menu"><a href="logout.jsp">Logout</a></li>
    <li id="users"><a href="users.jsp">Users</a></li>
    <li class="menu">
        <button class="menu" id="desc" onclick="showDesc()">Exercise description</button>
    </li>
</ul>

<input id="fileName" type="hidden" value=""/>
<section class="containerC">
    <menu id="ctxMenu">
        <menu id="addFile" title="Add file"></menu>
        <menu id="uploadProject" title="Upload project" onclick="uploadFiles()"></menu>
        <menu id="createNewVersion" title="Create new version from this" onclick="createNewVersion()"></menu>
        <menu id="runProject" title="Run project" onclick="runFiles()"></menu>
    </menu>
    <div id="projectStructure" class="projectStructureC"></div>

    <pre id="editor">
        Welcome to code editor!

        Exercise = <%=ex%> version <%=version%>
    </pre>
</section>

<button class="regular" id="uploadFiles" onclick="uploadFiles()">Upload codes</button>
<button class="regular" id="createNewProjectVersion" onclick="createNewVersion()">Create version <%=(version + 1)%> from
    this state
</button>
<button class="regular" id="runFiles" onclick="runFiles()">Run codes</button>

</body>
<script type="text/javascript">
    if (<%=teacher%>) {
        document.getElementById("users").className = "menu";
    } else {
        document.getElementById("users").className = "hide";
    }
</script>

</html>


