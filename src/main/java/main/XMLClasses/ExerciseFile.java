package main.XMLClasses;

import org.w3c.dom.Element;

public class ExerciseFile {
    private final String type;
    private final String name;
    private final boolean visible;
    private final boolean isMasterTest;

    public ExerciseFile(Element file_element) {
        type = file_element.getAttribute("type");
        name = file_element.getAttribute("name");
        visible = file_element.getAttribute("visible").equals("true") ? true : false;
        isMasterTest = file_element.getAttribute("type").equals("master_test") ? true : false;
    }

    public String getType() {
        return type;
    }


    public String getName() {
        return name;
    }

    public String getNameWithoutFileType() {
        return name.substring(0, name.lastIndexOf("."));
    }

    public boolean isVisible() {
        return visible;
    }

    public boolean isMasterTest() {
        return isMasterTest;
    }

    public boolean isUserTest() {
        return type.equals("test");
    }

    public boolean isCode() {
        return type.equals("code");
    }
}
