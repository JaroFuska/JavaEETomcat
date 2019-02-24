package main.XMLClasses;

import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import java.util.ArrayList;
import java.util.List;

public class Level {
    private final List<LevelTest> tests;
    private final String description;
    private final String id;

    public Level(String exercise_type, Element level_element) {
        id = level_element.getAttribute("id");
        description= level_element.getTextContent().trim();
        tests = new ArrayList<>();
        if (exercise_type.equals("LEGACY_CODE")) {
            NodeList test_list = level_element.getElementsByTagName("test");
            for (int i = 0; i < test_list.getLength(); i++) {
                Node test = test_list.item(i);
                tests.add(new LevelTest((Element) test));
            }
        }
    }

    public List<LevelTest> getTests() {
        return tests;
    }

    public String getDescription() {
        return description;
    }

    public String getId() {
        return id;
    }
}
