package main;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.InputSource;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import java.io.InputStream;
import java.io.StringReader;
import java.util.ArrayList;
import java.util.Map;

public class Exercise {

    private String id;
    private String type;
    private Map<Integer, String> levels;
    private ArrayList<File> files;

    public Exercise(String config_file) {
        try {
            Document xmlDoc = loadXMLFromString(config_file);
            Element root = xmlDoc.getDocumentElement();
            id = root.getAttribute("id");
            type = root.getAttribute("type");
            NodeList levels_list = root.getElementsByTagName("level");
            for (int i = 0; i < levels_list.getLength(); i++) {
                Node level = levels_list.item(i);
                String id = ((Element)level).getAttribute("id");
                String desc = level.getFirstChild().getTextContent();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    private static Document loadXMLFromString(String xml) throws Exception {
        DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
        DocumentBuilder builder = factory.newDocumentBuilder();
        InputSource is = new InputSource(new StringReader(xml));
        return builder.parse(is);
    }

    public static void main(String[] args) {
        String xmlFile = "<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n" +
                "\n" +
                "<exercise type=\"TDD\" id=\"4\">\n" +
                "    <levels number=\"2\">\n" +
                "        <level id=\"1\"><description>Uloha 1: Napis metodu \"getLastCharsOfString(inputStirng, numberOfLastChars)\", ktora vrati poslednych \"numberOfLastChars\" stringu \"inputString\"" +
                "            </description>\n" +
                "        </level>\n" +
                "        <level id=\"2\">\n" +
                "            <description>Uloha 2: Napis metodu \"getSuffixAfterChar(inputString, char)\", ktora vrati cast stringu \"inputString\", ktora nasleduje po znaku \"char\"\n" +
                "            </description>\n" +
                "        </level>\n" +
                "    </levels>\n" +
                "    <files>\n" +
                "        <file type=\"test\" name=\"test.py\" visible=\"true\"/>\n" +
                "        <file type=\"master_test\" name=\"masterTest.py\" visible=\"false\"/>\n" +
                "        <file type=\"code\" name=\"stringOperations.py\" visible=\"true\"/>\n" +
                "        <file type=\"setup\" name=\"requirements.txt\" visible=\"false\"/>\n" +
                "        <file type=\"setup\" name=\"Dockerfile\" visible=\"false\"/>\n" +
                "    </files>\n" +
                "</exercise>";

        Document xmlDoc = null;
        try {
            xmlDoc = loadXMLFromString(xmlFile);
            Element root = xmlDoc.getDocumentElement();
            System.out.println();
            NodeList levels_list = root.getElementsByTagName("level");
            for (int i = 0; i < levels_list.getLength(); i++) {
                Node level = levels_list.item(i);
                String desc = level.getFirstChild().getTextContent();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
