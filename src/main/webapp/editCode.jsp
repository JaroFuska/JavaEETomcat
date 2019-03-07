<%@ page import="java.io.File" %>
<%@ page import="dbmanager.DbManager" %>
<%@ page import="main.User" %>
<%@ page import="main.XMLClasses.Exercise" %>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%
    String root = request.getParameter("root");
    request.getSession().setAttribute("root", root);
    User user = (User) request.getSession().getAttribute("user");
    Exercise exercise = (Exercise) request.getSession().getAttribute("exercise");
    if (user == null) {
        response.sendRedirect("index.jsp");
    }
    boolean teacher = user.isTeacher();
    String ex = (String) request.getSession().getAttribute("ex");
    DbManager db = new DbManager();
    String exerciseDesc = db.getExerciseDesc(ex);
    if (exercise.getType().toUpperCase().contains("LEGACY")) {
        exerciseDesc = "Exercise " + ex + " - " + exerciseDesc;
    }
    int version = Integer.parseInt((String) request.getSession().getAttribute("version"));
%>
<html lang="en">
<head>
    <title>Code editor</title>
    <link rel="stylesheet" type="text/css" href="/CSS/main.css">
    <link rel="stylesheet" type="text/css" href="/CSS/editCode.css">
    <link rel="stylesheet" type="text/css" href="/CSS/loading-bar.css">
    <meta http-equiv="Content-Type" content="text/html;charset=utf-8"/>


    <script src="jquery.fileTree/jquery.js" type="text/javascript"></script>
    <script src="jquery.fileTree/jquery.easing.js" type="text/javascript"></script>
    <script src="jquery.fileTree/jqueryFileTree.js" type="text/javascript"></script>
    <script src="ace/ace.js" type="text/javascript" charset="utf-8"></script>
    <link href="jquery.fileTree/jqueryFileTree.css" rel="stylesheet" type="text/css" media="screen"/>

</head>

<body>
<ul class="menu">
    <li class="menu"><a href="exercises.jsp">Exercises</a></li>
    <li class="menu"><a href="settings.jsp">Settings</a></li>
    <li class="menu"><a href="logout.jsp">Logout</a></li>
    <li id="users"><a href="users.jsp">Users</a></li>
</ul>

<%--<div class="tab">--%>
<%--<button class="tablinks" onclick="managePage(event, 'workflowTab')" id="defaultOpen">Exercise workflow</button>--%>
<%--<button class="tablinks" onclick="managePage(event, 'editorTab')">Code editor</button>--%>
<%--</div>--%>

<div class="level">
    <h1 id="exerciseHeader">
        Exercise <%=ex%>
    </h1>
    <h4><%=exerciseDesc%>
    </h4>
    <p></p><br>
</div>

<div id="leg_code_container">
    <ol class="progtrckr" data-progtrckr-steps="6">
        <li id="pb1" class="progtrckr-now">Documentation</li>
        <li id="pb2" class="progtrckr-todo">Tests</li>
        <li id="pb3" class="progtrckr-todo">Implementation</li>
        <li id="pb4" class="progtrckr-todo">Refactoring</li>
        <li id="pb5" class="progtrckr-todo">Documentation</li>
        <li id="pb6" class="progtrckr-todo">Tests</li>
    </ol>


    <p id="leg_code_step_desc">Write documentation for methods that will be affected by change</p>
    <button class="regular" id="legacy_code_step" onclick="showLCStep()">Next step</button>
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
    <div id="workflowPanel">
        <div>
            <p id="stateDesc"></p>
        </div>
        <div id="traffic-light">
            <div id="stopLight" class="bulb"></div>
            <div id="goLight" class="bulb"></div>
            <div id="slowLight" class="bulb"></div>
        </div>
        <button class="regular" id="tdd_exercise_workflow" onclick="testWorkflow('<%=root%>')">New tests implemented
        </button>
    </div>
    <button class="regular" id="uploadFiles" onclick="uploadFiles()">Upload codes</button>
    <button class="regular" id="createNewProjectVersion" onclick="createNewVersion()">Create version <%=(version + 1)%>
        from
        this state
    </button>
    <button class="regular" id="runFiles" onclick="runFiles()">Run codes</button>
</section>

</body>
<script type="text/javascript">
    function elementByID(elementID) {
        return document.getElementById(elementID);
    }
    if (<%=teacher%>) {
        elementByID("users").className = "menu";
    } else {
        elementByID("users").className = "hide";
    }
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
                elementByID("fileName").value = file;
                $.post('/main.java.servlets.SetEditorTextServlet?fileName=' + file, function (data) {
                    editor.session.setValue(data, -1);
                });
            }
        });
    });

    var levelsDesc = [];
    var files_types = new Map();
    var exerciseType = '<%=exercise.getType()%>';
    <%
    for (int lev : exercise.getLevels().keySet()) { %>
        levelsDesc[<%=lev%>] = '<%=exercise.getLevels().get(lev).getDescription()%>';
    <%
    }
    for (String file_name : exercise.getFiles().keySet()) {
        String type = exercise.getFiles().get(file_name).getType(); %>
        files_types.set('<%=file_name%>', '<%=type%>');
    <%
    }
    %>
</script>
<script src="JS/editCode.js" type="text/javascript" charset="utf-8"></script>

<script type="text/javascript">
    var step = 1;
    //TODO take level from DB - personalize for student where he ended
    var level = 1;
    if (exerciseType != 'TDD') {
        elementByID('workflowPanel').style.display = 'none';
        elementByID('exerciseHeader').style.display = 'none';
        elementByID('editor').style.width = "80%";
        if (exerciseType == 'LEGACY_CODE') {
            // showLCStep();
        }

    }
    if (exerciseType == 'TDD') {
        showTDDLevel(level);
        document.getElementById('leg_code_container').style.display = 'none';
    }

    function showTDDLevel(level) {
        clearLights();
        illuminateRed();
        var levelDiv = document.getElementsByClassName("level")[0];
        var descriptionElement = levelDiv.getElementsByTagName("p")[0];
        descriptionElement.innerHTML = levelsDesc[level];
        //    TODO if there is no more level
        //    submit my solution
    }

    function showLegacyCodeLevel(level) {
        var levelDiv = document.getElementsByClassName("level")[0];
        var descriptionElement = levelDiv.getElementsByTagName("p")[0];
        descriptionElement.innerHTML = levelsDesc[level];
        step = 1;
        //TODO  show some stage panel
        //    TODO if there is no more level
        //    submit my solution
    }

    function showLCStep() {
        switch (step) {
            case 1:
                elementByID('pb1').className = "progtrckr-done";
                elementByID('pb2').className = "progtrckr-now";
                elementByID('leg_code_step_desc').innerHTML = 'Implement tests for methods that will be affected by change';
                elementByID('legacy_code_step').innerHTML = "Next step";
                break;
            case 2:
                elementByID('pb2').className = "progtrckr-done";
                elementByID('pb3').className = "progtrckr-now";
                elementByID('leg_code_step_desc').innerHTML = 'Implement recommended changes to code';
                break;
            case 3:
                elementByID('pb3').className = "progtrckr-done";
                elementByID('pb4').className = "progtrckr-now";
                elementByID('leg_code_step_desc').innerHTML = 'Code refactoring';
                break;
            case 4:
                elementByID('pb4').className = "progtrckr-done";
                elementByID('pb5').className = "progtrckr-now";
                elementByID('leg_code_step_desc').innerHTML = 'Write documentation for methods that you implemented';
                break;
            case 5:
                elementByID('pb5').className = "progtrckr-done";
                elementByID('pb6').className = "progtrckr-now";
                elementByID('leg_code_step_desc').innerHTML = 'Write tests for methods that you implemented';
                break;
            case 6:
                elementByID('leg_code_step_desc').innerHTML = 'Well done! Now you can move to next level';
                elementByID('pb6').className = "progtrckr-done";
                elementByID('legacy_code_step').innerHTML = "Next level";
                break;
            case 7:
                step = 0;
                elementByID('pb1').className = "progtrckr-now";
                elementByID('pb2').className = "progtrckr-todo";
                elementByID('pb3').className = "progtrckr-todo";
                elementByID('pb4').className = "progtrckr-todo";
                elementByID('pb5').className = "progtrckr-todo";
                elementByID('pb6').className = "progtrckr-todo";
                elementByID('leg_code_step_desc').innerHTML = 'Write documentation for methods that will be affected by change';
                //TODO - show next level description
                break;
        }
        step++;

    }



</script>

</html>


