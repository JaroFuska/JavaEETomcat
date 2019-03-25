package main.XMLClasses;

import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import java.util.*;

public class Level {
    private final List<LevelMethod> methods;
    private final List<LevelStep> steps;
    private final Map<Integer, String> stepsDesc;
    private final String description;
    private final String id;

    public Level(String exercise_type, Element level_element) {
        id = level_element.getAttribute("id");
        description= level_element.getTextContent().trim();
        methods = new ArrayList<>();
        steps = new ArrayList<>();
        if (exercise_type.equals("LEGACY_CODE")) {
            NodeList test_list = level_element.getElementsByTagName("method");
            for (int i = 0; i < test_list.getLength(); i++) {
                Node test = test_list.item(i);
                methods.add(new LevelMethod((Element) test));
            }
        }
        if (exercise_type.equals("REFACTORING")) {
            NodeList step_list = level_element.getElementsByTagName("step");
            for (int i = 0; i < step_list.getLength(); i++) {
                Node step = step_list.item(i);
                steps.add(new LevelStep((Element) step));
            }
        }
        stepsDesc = new TreeMap<>();
        for (LevelStep step : steps) {
            stepsDesc.put(step.getIntId(), step.getDescription());
        }
    }

    public List<LevelMethod> getMethods() {
        return methods;
    }

    public List<LevelStep> getSteps() {
        return steps;
    }

    public Map<Integer, String> getStepsDesc() {
        return stepsDesc;
    }

    public String getDescription() {
        return description;
    }

    public String getId() {
        return id;
    }
}
