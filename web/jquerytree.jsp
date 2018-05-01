<%@ page import="java.io.File" %>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%
    String root = request.getParameter("root");
%>
<html lang="en">
<head>
    <title>jQuery File Tree Demo</title>
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
                    // TODO handle save
                    alert(editor.session.getValue());
                }
            });
            editor.on("change", function () {
                editedFiles.set(currentFile, editor.session.getValue());
                // alert(editedFiles.size);
            });
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
                loadMessage: 'Please wait...'
            }, function (file) {
                currentFile = file;
                <%-- TODO save file to map only if it changed and opening a new file --%>
                // editedFiles.set(file, editor.session.getValue());
                if (editedFiles.has(file)) {
                    editor.session.setValue(editedFiles.get(file), -1);
                } else {
                    document.getElementById("fileName").value = file;
                    $.post('/SetEditorTextServlet?fileName=' + file, function (data) {
                        // $('#editor').text(data);
                        // document.getElementById("editor").innerHTML = data;
                        // alert(document.getElementById("editor"));
                        editor.session.setValue(data, -1);
                        // editor.setTheme("ace/theme/twilight");
                        // editor.session.setMode("ace/mode/python");
                    });
                }
            });
        });


        function uploadFiles() {
            //    TODO - upload edited code to db.
            // var blob = new Blob([editor.session.getValue()], {type: "text/plain;charset=utf-8"});
            // alert(blob);

            for (let [key, value] of editedFiles) {
                alert(key);
                alert(value);
                $.ajax({
                    url: '/SaveCodeServlet',
                    data: {
                        key: key,
                        value: value
                    },
                    type: 'POST'
                });
            }


            // Object.keys(editedFiles).forEach(function(key) {
            //     alert(key);
            //     alert(editedFiles[key]);
            //     $.ajax({
            //         url: '/SaveCodeServlet',
            //         data: {
            //             key: key,
            //             value: editedFiles[key]
            //         },
            //         type: 'POST'
            //     });
            // });

        }

    </script>


</head>

<body>


<input id="fileName" type="hidden" value=""/>
<section class="containerC">
    <div id="projectStructure" class="projectStructureC"></div>

    <pre id="editor">
    #Add python CODE here!

    def func():
        for i in range(20):
            print(i)
    </pre>
</section>

<button id="uploadFiles" onclick="uploadFiles()">Upload codes</button>
<script>
</script>

</body>


</html>