<%@ page import="dbmanager.DbManager" %>
<%@ page import="main.User" %>
<%@ page import="main.XMLClasses.Exercise" %>
<%@ page import="java.util.Map" %>
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
    if (exercise.getType().toUpperCase().contains("LEGACY") || exercise.getType().toUpperCase().contains("REFACTOR")) {
        exerciseDesc = "Exercise " + ex + " - " + exerciseDesc;
    }
    int version = Integer.parseInt((String) request.getSession().getAttribute("version"));
    int lastVersion = db.getUserLastVersion(Integer.parseInt(exercise.getId()), user.getUser_id());
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
    <p id="leg_code_level_desc">Find methods that will be affected by change (used/modified...)</p>
    <ol class="progtrckr" data-progtrckr-steps="7">
        <li id="pb0" class="progtrckr-now">Know your enemy</li>
        <li id="pb1" class="progtrckr-todo">Documentation</li>
        <li id="pb2" class="progtrckr-todo">Tests</li>
        <li id="pb3" class="progtrckr-todo">Implementation</li>
        <li id="pb4" class="progtrckr-todo">Refactoring</li>
        <li id="pb5" class="progtrckr-todo">Documentation</li>
        <li id="pb6" class="progtrckr-todo">Tests</li>
    </ol>


    <p id="leg_code_step_desc">Find methods that will be affected by change (used/modified...)</p>
    <button class="regular" id="legacy_code_step" onclick="showLCStep('<%=root%>')">Next step</button>
</div>

<div id="refactoring_container">
    <ol id="refactoring_prog_bar" class="progtrckr" data-progtrckr-steps="0">
    </ol>


    <p id="refactoring_step_desc"></p>
    <button class="regular" id="refactoring_step" onclick="showRefactorStep('<%=root%>')">Next step</button>
</div>


<section id="editorTab" class="containerC">
    <input id="fileName" type="hidden" value=""/>
    <menu id="ctxMenu">
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
    <button class="regular" id="runFiles" onclick="runFiles()">Run codes</button>
    <button class="regular" id="createNewProjectVersion" onclick="createNewVersion()">Create version <%=(lastVersion + 1)%>
        from
        this state
    </button>
    <button class="regular" id="chooseVersion"onclick="chooseVersion()">Choose version to edit</button>
    <div id="versionSelectDiv" style="display:none;border: 3px solid black;padding: 10px;background: gray;">
    <select id="versionSelect" style="display:none">
    </select>
    <button class="regular" id="openSelectedVersion" onclick="openVersion()" style="display:none;color: white;background: black;border: 1px solid lightgray;">Edit this version</button>
    </div>
</section>

</body>
<script type="text/javascript">
    for (i = 1; i <= <%=lastVersion%>; i++) {
        var versionItem = document.createElement("OPTION");
        versionItem.value = i + "";
        var t = document.createTextNode("Version " + i);
        versionItem.appendChild(t);
        document.getElementById("versionSelect").appendChild(versionItem);
    }
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
    var stepsDesc = new Map();
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
    if (exercise.getType().toUpperCase().contains("REFACTOR")) {
        for (int lev : exercise.getLevels().keySet()) { %>
    var descriptions = new Map(); <%
            for (Map.Entry<Integer, String> entry : exercise.getLevel(lev).getStepsDesc().entrySet()) {%>
    descriptions.set(<%=entry.getKey()%>, '<%=entry.getValue()%>');
    <%
            } %>
                stepsDesc.set(<%=lev%>, descriptions);
    <%
        }
    }
    %>
</script>
<script src="JS/editCode.js" type="text/javascript" charset="utf-8"></script>

<script type="text/javascript">
    var step = 0;
    var refSteps = 0;
    var level = 1;
    if (exerciseType != 'TDD') {
        elementByID('workflowPanel').style.display = 'none';
        elementByID('exerciseHeader').style.display = 'none';
        elementByID('editor').style.width = "80%";
        if (exerciseType == 'LEGACY_CODE') {
            // showLCStep();
        }
        if (exerciseType == 'REFACTORING') {
            step = 1;
        }

    }
    if (exerciseType == 'TDD') {
        showTDDLevel(level);
        document.getElementById('leg_code_container').style.display = 'none';
        document.getElementById('refactoring_container').style.display = 'none';
    }

    if (exerciseType == 'REFACTORING') {
        showRefactoringLevel(level);
        document.getElementById('leg_code_container').style.display = 'none';
    }

    if (exerciseType == 'LEGACY_CODE') {
        showLCLevel(level);
        document.getElementById('refactoring_container').style.display = 'none';
    }

    function showTDDLevel(level) {
        if (level >= levelsDesc.length) {
            alert('Congratulations! You finished the last level! Feel free to do another exercise.');
        } else {
            clearLights();
            var levelDiv = document.getElementsByClassName("level")[0];
            var descriptionElement = levelDiv.getElementsByTagName("p")[0];
            descriptionElement.innerHTML = levelsDesc[level];
        }
    }

    function showLCLevel(level) {
        if (level >= levelsDesc.length) {
            alert('Congratulations! You finished the last level! Feel free to do another exercise.');
        } else {
            elementByID('leg_code_level_desc').innerHTML = 'Level ' + level + ' - ' + levelsDesc[level];
            step = 0;
        }
    }

    function showRefactoringLevel(level) {
        if (level >= levelsDesc.length) {
            alert('Congratulations! You finished the last level! Feel free to do another exercise.');
        } else {
            var levelDiv = document.getElementsByClassName("level")[0];
            var descriptionElement = levelDiv.getElementsByTagName("p")[0];
            descriptionElement.innerHTML = levelsDesc[level];
            step = 1;
            var olElem = elementByID('refactoring_prog_bar');
            while (olElem.hasChildNodes()) {
                olElem.removeChild(olElem.firstChild);
            }
            var i;
            for (i = 1; i < stepsDesc.get(level).size + 1; i++) {
                var liElem = document.createElement("LI");
                liElem.className = 'progtrckr-todo';
                liElem.id = 'refStep' + i;
                liElem.innerHTML = 'Step ' + i;
                olElem.appendChild(liElem);
            }
            i--;
            refSteps = i;
            elementByID("refactoring_prog_bar").setAttribute('data-progtrckr-steps', i + "");
            elementByID("refStep1").className = "progtrckr-now";
            elementByID("refactoring_step_desc").innerHTML = stepsDesc.get(level).get(1);
        }
    }

    function showRefactorStep(root) {
        uploadFiles();
        if (step <= refSteps) {
            $.ajax({
                url: '/main.java.servlets.RefactoringStepServlet',
                data: {
                    fileName: root,
                    level: level,
                    step: step
                },
                async: false,
                type: 'POST',
                success: function (data) {
                    if (data.includes('FAIL')) {
                        alert(data);
                    } else {
                        elementByID("refStep" + step).className = "progtrckr-done";
                        step++;
                        if (step > refSteps) {
                            elementByID("refactoring_step").innerHTML = "Next level"
                        } else {
                            elementByID("refStep" + step).className = "progtrckr-now";
                            elementByID("refactoring_step_desc").innerHTML = stepsDesc.get(level).get(step);
                        }
                    }
                }
            });

        } else {
            level++;
            showRefactoringLevel(level);
            elementByID("refactoring_step").innerHTML = "Next step"
        }
    }

    function showLCStep(root) {
        uploadFiles();
        switch (step) {
            case 0:
                $.ajax({
                    url: '/main.java.servlets.MethodsSelectorServlet',
                    data: {
                        fileName: root,
                        level: level
                    },
                    async: false,
                    type: 'POST',
                    success: function (data) {
                        if (data != "") {
                            alert(data);
                        } else {
                            elementByID('pb0').className = "progtrckr-done";
                            elementByID('pb1').className = "progtrckr-now";
                            elementByID('leg_code_step_desc').innerHTML = 'Write documentation for methods that will be affected by change';
                            elementByID('legacy_code_step').innerHTML = "Next step";
                            step++;
                        }
                    }
                });
                break;
            case 1:
                //Documentation for former methods
                $.ajax({
                    url: '/main.java.servlets.CheckDocServlet',
                    data: {
                        fileName: root,
                        level: level,
                        step: step
                    },
                    async: false,
                    type: 'POST',
                    success: function (data) {
                        if (data != "OK") {
                            alert(data);
                        } else {
                            elementByID('pb1').className = "progtrckr-done";
                            elementByID('pb2').className = "progtrckr-now";
                            elementByID('leg_code_step_desc').innerHTML = 'Implement tests for methods that will be affected by change';
                            elementByID('legacy_code_step').innerHTML = "Next step";
                            step++;
                        }
                    }
                });
                break;
            case 2:
                //Tests for former methods
                $.ajax({
                    url: '/main.java.servlets.RunCodeServletLegacy',
                    data: {
                        fileName: root,
                        level: level,
                        step: step
                    },
                    async: false,
                    type: 'POST',
                    success: function (data) {
                        if (data != "OK") {
                            alert(data);
                        } else {
                            elementByID('pb2').className = "progtrckr-done";
                            elementByID('pb3').className = "progtrckr-now";
                            elementByID('leg_code_step_desc').innerHTML = 'Implement recommended changes to code';
                            step++;
                        }
                    }
                });
                break;
            case 3:
                //Implementation of new method
                $.ajax({
                    url: '/main.java.servlets.RunCodeServletLegacy',
                    data: {
                        fileName: root,
                        level: level,
                        step: step
                    },
                    async: false,
                    type: 'POST',
                    success: function (data) {
                        if (data.includes("FAIL")) {
                            alert(data);
                        } else {
                            elementByID('pb3').className = "progtrckr-done";
                            elementByID('pb4').className = "progtrckr-now";
                            elementByID('leg_code_step_desc').innerHTML = 'Code refactoring';
                            step++;
                        }
                    }
                });
                break;
            case 4:
                //Refactoring
                $.ajax({
                    url: '/main.java.servlets.RunCodeServletLegacy',
                    data: {
                        fileName: root,
                        level: level,
                        step: step
                    },
                    async: false,
                    type: 'POST',
                    success: function (data) {
                        if (data.includes("FAIL")) {
                            alert(data);
                        } else {
                            elementByID('pb4').className = "progtrckr-done";
                            elementByID('pb5').className = "progtrckr-now";
                            elementByID('leg_code_step_desc').innerHTML = 'Write documentation for methods that you implemented';
                            step++;
                        }
                    }
                });
                break;
            case 5:
                //Documentation of new method
                $.ajax({
                    url: '/main.java.servlets.CheckDocServlet',
                    data: {
                        fileName: root,
                        level: level,
                        step: step
                    },
                    async: false,
                    type: 'POST',
                    success: function (data) {
                        if (data != "OK") {
                            alert(data);
                        } else {
                            elementByID('pb5').className = "progtrckr-done";
                            elementByID('pb6').className = "progtrckr-now";
                            elementByID('leg_code_step_desc').innerHTML = 'Write tests for methods that you implemented';
                            step++;
                        }
                    }
                });
                break;
            case 6:
                //Tests for new methods
                $.ajax({
                    url: '/main.java.servlets.RunCodeServletLegacy',
                    data: {
                        fileName: root,
                        level: level,
                        step: step
                    },
                    async: false,
                    type: 'POST',
                    success: function (data) {
                        if (data != "OK") {
                            alert(data);
                        } else {
                            elementByID('leg_code_step_desc').innerHTML = 'Well done! Now you can move to next level';
                            elementByID('pb6').className = "progtrckr-done";
                            elementByID('legacy_code_step').innerHTML = "Next level";
                            step++;
                        }
                    }
                });
                break;
            case 7:
                elementByID('pb0').className = "progtrckr-now";
                elementByID('pb1').className = "progtrckr-todo";
                elementByID('pb2').className = "progtrckr-todo";
                elementByID('pb3').className = "progtrckr-todo";
                elementByID('pb4').className = "progtrckr-todo";
                elementByID('pb5').className = "progtrckr-todo";
                elementByID('pb6').className = "progtrckr-todo";
                elementByID('leg_code_step_desc').innerHTML = 'Find methods that will be affected by change (used/modified...)';
                level++;
                showLCLevel(level);
                break;
        }


    }


</script>

</html>


