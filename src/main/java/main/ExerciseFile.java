package main;

import org.w3c.dom.Element;

public class ExerciseFile {
    private final String type;
    private final String name;
    private final boolean visible;

    public ExerciseFile(Element file_element) {
        type = file_element.getAttribute("type");
        name = file_element.getAttribute("name");
        visible = file_element.getAttribute("visible").equals("true") ? true : false;
    }

    public String getType() {
        return type;
    }

    public String getName() {
        return name;
    }

    public boolean isVisible() {
        return visible;
    }

}
