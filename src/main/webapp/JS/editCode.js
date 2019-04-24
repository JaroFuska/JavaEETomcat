var editor;
var editedFiles = new Map();
var currentFile = null;
var state = 0;
$(window).load(function () {
    editor = ace.edit("editor");
    editor.setTheme("ace/theme/twilight");
    editor.session.setMode("ace/mode/python");
    editor.commands.addCommand({
        name: 'save',
        bindKey: {win: "Ctrl-S", "mac": "Cmd-S"},
        exec: function (editor) {
            uploadFiles();
        }
    });
    editor.on("change", function () {
        if (currentFile != null) {
            if (!Object.is(files_types.get(currentFile.substring(currentFile.lastIndexOf('/') + 1)), undefined)) {
                var currentFileType = files_types.get(currentFile.substring(currentFile.lastIndexOf('/') + 1));
                if ((exerciseType == 'TDD' && state == 0) || (exerciseType == 'LEGACY_CODE' && step == 2)) {
                    if (currentFileType == 'code') {
                        editor.setReadOnly(true);
                    } else {
                        editor.setReadOnly(false);
                    }
                } else {
                    editor.setReadOnly(false);
                }
            }
        }
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
    }, false);
    projectStructure.addEventListener("click", function (event) {
        var ctxMenu = document.getElementById("ctxMenu");
        ctxMenu.style.display = "";
        ctxMenu.style.left = "";
        ctxMenu.style.top = "";
    }, false);


})


function uploadFiles() {
    for (let [key, value] of editedFiles) {
        $.ajax({
            url: '/main.java.servlets.SaveCodeServlet',
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
    $.post('/main.java.servlets.CreateNewVersionServlet', function (data) {
        location.reload(true);
    });
}

function chooseVersion() {
    if (elementByID("versionSelectDiv").style.display == 'none') {
        elementByID("versionSelectDiv").style.display = 'block';
    } else {
        elementByID("versionSelectDiv").style.display = 'none';
    }
    $("#versionSelect").toggle();
    $("#openSelectedVersion").toggle();
}

function openVersion() {
    var sel = elementByID("versionSelect");
    var ver = sel.options[sel.selectedIndex].value;
    $.ajax({
        url: '/main.java.servlets.CreateProjectServlet',
        data: {
            version: ver,
            versionChange: 'true'
        },
        async: false,
        type: 'POST',
        success: function (data) {
            if (data == "") {
                alert("Not valid request");
            } else {
                document.location.href = '/editCode.jsp?root=' + data;
            }
        }
    });
}

function runFiles() {
    uploadFiles();
    $.ajax({
        url: '/main.java.servlets.RunCodeServlet',
        data: {
            fileName: currentFile
        },
        async: false,
        type: 'POST',
        success: function (data) {
            alert(data)
        }
    });

}

window.onbeforeunload = function () {
    if (editedFiles.size > 0) {
        return 'You have unsaved code';
    } else {
        return;
    }
};


function testWorkflow(root) {
    uploadFiles();
    switch (state) {
        case 0:
            var testResult = '';
            $.ajax({
                url: '/main.java.servlets.RunCodeServletTDD',
                data: {
                    fileName: root,
                    testType: "test"
                },
                async: false,
                type: 'POST',
                success: function (data) {
                    testResult = data;
                }
            });
            if (testResult.includes('FAIL')) {
                document.getElementById('tdd_exercise_workflow').innerHTML = 'Method implemented';
                illuminateRed();
                state = 1;
            } else {
                alert('Write some test to method that will fail!');
            }
            break;
        case 1:
            //check if method was implemented (run his tests) - if did, run super tests to test method
            var testResult = '';
            $.ajax({
                url: '/main.java.servlets.RunCodeServletTDD',
                data: {
                    fileName: root,
                    testType: "test"
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
                    url: '/main.java.servlets.RunCodeServletTDD',
                    data: {
                        fileName: root,
                        testType: "master_test",
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
                    document.getElementById('tdd_exercise_workflow').innerHTML = 'Done refactoring';
                    // document.getElementById('2:method_implemented').checked = true;
                    // document.getElementById('3:test_passed').checked = true;
                    illuminateGreen();
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
            // Refactoring
            $.ajax({
                url: '/main.java.servlets.RunCodeServletTDD',
                data: {
                    fileName: root,
                    testType: "both",
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
                illuminateOrange();
                state = 3;
            } else {
                // master tests don't pass
                alert(testResult);
            }
            break;
        case 3:
            // Initialize next level
            level = level + 1;
            showTDDLevel(level);
            document.getElementById('tdd_exercise_workflow').innerHTML = 'New tests implemented';
            state = 0;
            break;
    }
}

function illuminateRed() {
    document.getElementById('stopLight').style.backgroundColor = "red";
    document.getElementById('stateDesc').innerHTML = 'Write code that will pass through tests';
}

function illuminateGreen() {
    document.getElementById('stopLight').style.backgroundColor = "#660000";
    document.getElementById('goLight').style.backgroundColor = "#00e600";
    document.getElementById('stateDesc').innerHTML = 'Refactor your code without changing functionality';
}

function illuminateOrange() {
    document.getElementById('stopLight').style.backgroundColor = "#660000";
    document.getElementById('goLight').style.backgroundColor = "#145214";
    document.getElementById('slowLight').style.backgroundColor = "orange";
    document.getElementById('stateDesc').innerHTML = 'Now you can move to next level';
}

function clearLights() {
    document.getElementById('stopLight').style.backgroundColor = "black";
    document.getElementById('slowLight').style.backgroundColor = "black";
    document.getElementById('goLight').style.backgroundColor = "black";
    document.getElementById('stateDesc').innerHTML = 'Write test that will fail';
}



