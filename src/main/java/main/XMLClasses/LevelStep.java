package main.XMLClasses;

import org.w3c.dom.Element;

public class LevelStep {
    private final String description;
    private final String id;

    public LevelStep(Element step) {
        description = step.getAttribute("description");
        id = step.getAttribute("id");
    }

    public String getDescription() {
        return description;
    }

    public String getId() {
        return id;
    }

    public int getIntId() {
        return Integer.parseInt(id);
    }
}
