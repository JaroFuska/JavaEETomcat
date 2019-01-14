<%@ page import="java.io.File" %>
<%@ page import="dbmanager.DbManager" %>
<%@ page import="main.User" %>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%
    String root = request.getParameter("root");
    request.getSession().setAttribute("root", root);
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
            display: none;
            height: 100%;
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

        window.onbeforeunload = function () {
            if (editedFiles.size > 0) {
                uploadFiles();
                return 'Save your code first';
            } else {
                return;
            }
        };
    </script>


</head>

<body>
<ul class="menu">
    <li class="menu"><a href="exercises.jsp">Exercises</a></li>
    <li class="menu"><a href="settings.jsp">Settings</a></li>
    <li class="menu"><a href="logout.jsp">Logout</a></li>
    <li id="users"><a href="users.jsp">Users</a></li>
</ul>

<div class="tab">
    <button class="tablinks" onclick="managePage(event, 'workflowTab')" id="defaultOpen">Exercise workflow</button>
    <button class="tablinks" onclick="managePage(event, 'editorTab')">Code editor</button>
</div>

<section id="editorTab" class="containerC">
    <input id="fileName" type="hidden" value=""/>
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
    <button class="regular" id="uploadFiles" onclick="uploadFiles()">Upload codes</button>
    <button class="regular" id="createNewProjectVersion" onclick="createNewVersion()">Create version <%=(version + 1)%>
        from
        this state
    </button>
    <button class="regular" id="runFiles" onclick="runFiles()">Run codes</button>
</section>

<section id="workflowTab" class="containerC">
    <h1>
        Exercise ${param.ex} workflow
    </h1>
    <h4><%=exerciseDesc%>
    </h4>
    <div class="level">
        <p></p>
        <input class="tdd_state" id="1:test_failed" type="checkbox" onclick="return false;"/> TEST FAILED <br>
        <input class="tdd_state" id="2:method_implemented" type="checkbox" onclick="return false;"/> METHOD IMPLEMENTED
        <br>
        <input class="tdd_state" id="3:test_passed" type="checkbox" onclick="return false;"/> TEST PASSED <br>
        <button class="regular" id="tdd_exercise_workflow" onclick="testWorkflow()">New tests implemented</button>
    </div>
</section>


</body>
<script type="text/javascript">
    document.getElementById("defaultOpen").click();

    function managePage(evt, tab) {
        // Declare all variables
        var i, tabcontent, tablinks;

        // Get all elements with class="tabcontent" and hide them
        tabcontent = document.getElementsByClassName("containerC");
        for (i = 0; i < tabcontent.length; i++) {
            tabcontent[i].style.display = "none";
        }

        // Get all elements with class="tablinks" and remove the class "active"
        tablinks = document.getElementsByClassName("tablinks");
        for (i = 0; i < tablinks.length; i++) {
            tablinks[i].className = tablinks[i].className.replace(" active", "");
        }

        // Show the current tab, and add an "active" class to the button that opened the tab
        document.getElementById(tab).style.display = "block";
        evt.currentTarget.className += " active";
    }

    var state = 0;
    //TODO take level from DB
    var level = 0;
    showLevel(level);

    function testWorkflow() {
        switch (state) {
            case 0:
                var testResult = '';
                $.ajax({
                    url: '/main.java.main.RunCodeServlet',
                    data: {
                        fileName: "<%=root%>/tests.py"
                    },
                    async: false,
                    type: 'POST',
                    success: function (data) {
                        testResult = data;
                    }
                });
                if (testResult.includes('FAIL')) {
                    document.getElementById('tdd_exercise_workflow').innerHTML = 'Method implemented';
                    document.getElementById('1:test_failed').checked = true;
                    state = 1;
                } else {
                    alert('Write some test to method that will fail!');
                }
                break;
            case 1:
                //check if method was implemented (run his tests) - if did run super tests to test method
                var testResult = '';
                $.ajax({
                    url: '/main.java.main.RunCodeServlet',
                    data: {
                        fileName: "<%=root%>/tests.py"
                    },
                    async: false,
                    type: 'POST',
                    success: function (data) {
                        testResult = data;
                    }
                });
                if (!testResult.includes('FAIL')) {
                    //runMasterTestsForThisLevel      TestStringMethods
                    $.ajax({
                        url: '/main.java.main.RunCodeServlet',
                        data: {
                            //TODO - run different file (masterTest)
                            fileName: "<%=root%>/tests.py",
                            level: level
                        },
                        async: false,
                        type: 'POST',
                        success: function (data) {
                            if (data == "") {
                                alert("There is no code yet!");
                            } else {
                                testResult = data;
                            }
                        }
                    });
                    if (!testResult.includes('FAIL')) {
                        document.getElementById('tdd_exercise_workflow').innerHTML = 'Move to next level';
                        document.getElementById('2:method_implemented').checked = true;
                        document.getElementById('3:test_passed').checked = true;
                        state = 2;
                    } else {
                        //local tests passed, master tests don't
                        alert(testResult);
                    }
                } else {
                    //local test don't pass
                    alert(testResult);
                }
                break;
            case 2:
                // Initialize next level
                level = level + 1;
                showLevel(level);
                state = 0;
                break;
        }
    }

    function showLevel(level) {
        var checkBoxes = document.getElementsByClassName("tdd_state");
        for (i = 0; i < checkBoxes.length; i++) {
            checkBoxes[i].checked = false;
        }
        //TODO get level description from DB
        if (level == 0) {
            var levelDiv = document.getElementsByClassName("level")[0];
            var descriptionElement = levelDiv.getElementsByTagName("p")[0];
            descriptionElement.innerHTML = "Uloha 1: Napis metodu \"getLastCharsOfString(inputStirng, numberOfLastChars)\", ktora vrati poslednych \"numberOfLastChars\" stringu \"inputString\"";
        }
        if (level == 1) {
            var levelDiv = document.getElementsByClassName("level")[0];
            var descriptionElement = levelDiv.getElementsByTagName("p")[0];
            descriptionElement.innerHTML = "Uloha 2: Napis metodu \"getSuffixAfterChar(inputString, char)\", ktora vrati cast stringu \"inputString\", ktora nasleduje po znaku \"char\"";
        }
    //    TODO if there is no more level
    //    submit my solution
    }

    if (<%=teacher%>) {
        document.getElementById("users").className = "menu";
    } else {
        document.getElementById("users").className = "hide";
    }
</script>

</html>


