package main.XMLClasses;

import org.w3c.dom.Element;

public class LevelTest {
    private final String name;
    private final String type;

    public LevelTest(Element test_element) {
        name = test_element.getAttribute("name");
        type = test_element.getAttribute("type");
    }

    public String getName() {
        return name;
    }

    public String getType() {
        return type;
    }
}
