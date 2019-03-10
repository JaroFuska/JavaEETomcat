package main.XMLClasses;

import org.w3c.dom.Element;

public class LevelMethod {
    private final String name;
    private final String type;

    public LevelMethod(Element method_element) {
        name = method_element.getAttribute("name");
        type = method_element.getAttribute("type");
    }

    public String getName() {
        return name;
    }

    public String getType() {
        return type;
    }
}
