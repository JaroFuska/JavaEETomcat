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

window.onbeforeunload = function () {
    if (editedFiles.size > 0) {
        uploadFiles();
        return 'Save your code first';
    } else {
        return;
    }
};

// function managePage(evt, tab) {
//     // Declare all variables
//     var i, tabcontent, tablinks;
//
//     // Get all elements with class="tabcontent" and hide them
//     tabcontent = document.getElementsByClassName("containerC");
//     for (i = 0; i < tabcontent.length; i++) {
//         tabcontent[i].style.display = "none";
//     }
//
//     // Get all elements with class="tablinks" and remove the class "active"
//     tablinks = document.getElementsByClassName("tablinks");
//     for (i = 0; i < tablinks.length; i++) {
//         tablinks[i].className = tablinks[i].className.replace(" active", "");
//     }
//
//     // Show the current tab, and add an "active" class to the button that opened the tab
//     document.getElementById(tab).style.display = "block";
//     evt.currentTarget.className += " active";
// }

var state = 0;
//TODO take level from DB
var level = 0;
showLevel(level);

function testWorkflow(root) {
    switch (state) {
        case 0:
            var testResult = '';
            $.ajax({
                url: '/main.java.main.RunCodeServlet',
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
                url: '/main.java.main.RunCodeServlet',
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
                    url: '/main.java.main.RunCodeServlet',
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
                url: '/main.java.main.RunCodeServlet',
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
}

function illuminateOrange() {
    document.getElementById('slowLight').style.backgroundColor = "orange";
}

function illuminateGreen() {
    document.getElementById('goLight').style.backgroundColor = "green";
}

function clearLights() {
    document.getElementById('stopLight').style.backgroundColor = "black";
    document.getElementById('slowLight').style.backgroundColor = "black";
    document.getElementById('goLight').style.backgroundColor = "black";
}

function showLevel(level) {
    clearLights();
    illuminateRed();
    // clearLights();
    // var checkBoxes = document.getElementsByClassName("tdd_state");
    // for (i = 0; i < checkBoxes.length; i++) {
    //     checkBoxes[i].checked = false;
    // }
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

