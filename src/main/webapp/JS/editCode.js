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
        if (currentFile != null) {
            if (!Object.is(files_types.get(currentFile.substring(currentFile.lastIndexOf('/') + 1)), undefined)) {
                var currentFileType = files_types.get(currentFile.substring(currentFile.lastIndexOf('/') + 1));
                if (state == 0) {
                    if (currentFileType == 'code') {
                        editor.setReadOnly(true);
                    } else {
                        editor.setReadOnly(false);
                    }
                } else {
                    //TODO - set tests read only when editing code and refactoring?
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

    });
}

function runFiles() {
    uploadFiles();
    //TODO - find main method and run that file
    $.post('/main.java.servlets.RunCodeServlet?fileName=' + currentFile, function (data) {
        alert(data);
    });
}

window.onbeforeunload = function () {
    if (editedFiles.size > 0) {
        uploadFiles();
        return 'Save your code first';
    } else {
        return;
    }
};


var state = 0;
//TODO take level from DB - personalize for student where he ended
var level = 1;
showLevel(level);

function testWorkflow(root) {
    uploadFiles();
    switch (state) {
        case 0:
            var testResult = '';
            $.ajax({
                url: '/main.java.servlets.RunCodeServlet',
                data: {
                    fileName: root + "/tests.py"
                },
                async: false,
                type: 'POST',
                success: function (data) {
                    testResult = data;
                }
            });
            if (testResult.includes('FAIL')) {
                document.getElementById('tdd_exercise_workflow').innerHTML = 'Method implemented';
                // document.getElementById('1:test_failed').checked = true;
                illuminateGreen();
                state = 1;
            } else {
                alert('Write some test to method that will fail!');
            }
            break;
        case 1:
            //check if method was implemented (run his tests) - if did run super tests to test method
            var testResult = '';
            $.ajax({
                url: '/main.java.servlets.RunCodeServlet',
                data: {
                    fileName: root + "/tests.py"
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
                    url: '/main.java.servlets.RunCodeServlet',
                    data: {
                        //TODO - run different file (masterTest)
                        fileName: root + "/tests.py",
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
                    illuminateOrange();
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
                url: '/main.java.servlets.RunCodeServlet',
                data: {
                    //TODO - run different file (masterTest)
                    fileName: root + "/tests.py",
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
                // document.getElementById('2:method_implemented').checked = true;
                // document.getElementById('3:test_passed').checked = true;
                state = 3;
            } else {
                // master tests don't pass
                alert(testResult);
            }
            break;
        case 3:
            // Initialize next level
            level = level + 1;
            showLevel(level);
            state = 0;
            break;
    }
}

function illuminateRed() {
    document.getElementById('stopLight').style.backgroundColor = "red";
    document.getElementById('stateDesc').innerHTML = 'Red light = write test that does not pass';
}

function illuminateOrange() {
    document.getElementById('slowLight').style.backgroundColor = "orange";
    document.getElementById('stateDesc').innerHTML = 'Orange light = refactor your code';
}

function illuminateGreen() {
    document.getElementById('goLight').style.backgroundColor = "green";
    document.getElementById('stateDesc').innerHTML = 'Green light = write method';
}

function clearLights() {
    document.getElementById('stopLight').style.backgroundColor = "black";
    document.getElementById('slowLight').style.backgroundColor = "black";
    document.getElementById('goLight').style.backgroundColor = "black";
}



