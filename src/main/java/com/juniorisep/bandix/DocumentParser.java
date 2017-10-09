package com.juniorisep.bandix;

import org.w3c.dom.*;
import org.xml.sax.SAXException;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.List;
import java.util.Map;

public class DocumentParser {

    private String filename;

    public DocumentParser(String filename) {
        this.filename = filename;
    }

    public String getFilename() {
        return filename;
    }

    public void setFilename(String filename) {
        this.filename = filename;
    }


    public void replace(Map<String, String> mapping) throws IOException, SAXException, ParserConfigurationException {

        XmlNode rootNode = generateRootNode();

        rootNode.replace(mapping);

        File file = new File(filename);
        PrintWriter writer = new PrintWriter(file);
        if (filename.contains(".xml")) {
            writer.println("<?xml version=\"1.0\" encoding=\"UTF-8\"?>");
        }
        writer.println(writeMiniXmlNode(rootNode));
        writer.flush();
        writer.close();
    }

    public void manageTables(List<Map<String, String>> tableMap) throws IOException, SAXException, ParserConfigurationException {

        XmlNode rootNode = generateRootNode();

        for (XmlNode currentNode : rootNode.getChildren()) {
            if (DocxNodeName.BODY.equals(currentNode.getName())) {
                for (XmlNode currentBodyNode : currentNode.getChildren()) {
                    if (DocxNodeName.TABLE.equals(currentBodyNode.getName())) {
                        try {
                            currentBodyNode = manageSingleTable(currentBodyNode, tableMap);
                        } catch (CloneNotSupportedException e) {
                            e.printStackTrace();
                        }
                    }
                }
            }
        }

        File file = new File(filename);
        PrintWriter writer = new PrintWriter(file);
        if (filename.contains(".xml")) {
            writer.println("<?xml version=\"1.0\" encoding=\"UTF-8\"?>");
        }
        writer.println(writeMiniXmlNode(rootNode));
        writer.flush();
        writer.close();
    }

    public XmlNode manageSingleTable(XmlNode table, List<Map<String, String>> tableMap) throws CloneNotSupportedException {
        System.out.println("Found single table");

        XmlNode keyLine = null;
        boolean actualRow = false;
        int actualIndex = 0;
        int index = 0;

        for (XmlNode tableNode : table.getChildren()) {
            System.out.println(tableNode.getName());
            String currentTableNodeText = tableNode.getGlobalText();

            for (Map.Entry<String, String> currentEntry : tableMap.get(0).entrySet()) {
                if (currentTableNodeText.contains(currentEntry.getKey())){
                    actualRow = true;
                    actualIndex = index;
                }

            }

            index++;

            if (actualRow) {
                keyLine = tableNode;
                break;
            }
        }

        if (!actualRow) return table;


        table.getChildren().remove(keyLine);

        for (Map<String, String> currentMap : tableMap) {
            System.out.println("Key line : " + keyLine.getGlobalText());

            XmlNode currentNode = new XmlNode();
            currentNode = keyLine.clone();


            System.out.print(currentMap.get("FIRSTNAME") + " : ");
            System.out.println("VALUE TO REPLACE : " + currentMap.get("FIRSTNAME") + currentNode.getGlobalText());

            currentNode.replace(currentMap);

            table.addChild(currentNode);
        }

        return table;
    }




    public XmlNode generateRootNode() throws ParserConfigurationException, IOException, SAXException {
        DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
        DocumentBuilder builder = factory.newDocumentBuilder();
        Document document = builder.parse(filename);

        Element root = document.getDocumentElement();

        return createNode(root);
    }












    public XmlNode createNode(Node n) {

        if ("#text".equals(n.getNodeName())) return null;

        NodeList nodeList = n.getChildNodes();
        XmlNode node = new XmlNode(n.getNodeName());


        if (n.getAttributes()  != null && n.getAttributes().getLength() > 0) {
            NamedNodeMap att = n.getAttributes();
            for (int it = 0; it < att.getLength(); it++) {
                Node attributeNode = att.item(it);
                node.addAttribute(attributeNode.getNodeName(), attributeNode.getNodeValue());
            }
        }

        if ("w:t".equals(n.getNodeName())) {
            node.setText(n.getTextContent());
            return node;
        }

        for (int it = 0; it < nodeList.getLength(); it++) {
            Node currentNode = nodeList.item(it);

            XmlNode newNode = createNode(currentNode);

            if (newNode == null) {
                node.setText(currentNode.getTextContent());
            } else {
                node.addChild(newNode);
            }


        }

        return node;
    }

    public String displayXmlNode(XmlNode node, String tab) {
        StringBuilder value = new StringBuilder();

        value.append(tab).append("<").append(node.getName());

        if (node.getAttributes() != null) {
            node.getAttributes().forEach((k, v) -> {
                value.append(" ").append(k).append("=\"").append(v).append("\"");
            });
        }

        value.append(">");

        if (node.getChildren() != null) {
            value.append("\n");
            node.getChildren().forEach(child -> {
                value.append(displayXmlNode(child, tab + " "));
            });
            value.append(tab);
        } else if (node.getText() != null) {
            value.append(node.getText());
        } else {
            value.append("\n");
            value.append(tab);
        }

        value.append("</").append(node.getName()).append(">\n");

        return value.toString();
    }

    public String writeMiniXmlNode(XmlNode node) {
        StringBuilder value = new StringBuilder();

        value.append("<").append(node.getName());

        if (node.getAttributes() != null) {
            node.getAttributes().forEach((k, v) -> {
                value.append(" ").append(k).append("=\"").append(v).append("\"");
            });
        }

        value.append(">");

        if (node.getChildren() != null) {
            node.getChildren().forEach(child -> {
                value.append(writeMiniXmlNode(child));
            });
        } else if (node.getText() != null) {
            value.append(node.getText());
        }

        value.append("</").append(node.getName()).append(">");

        return value.toString();
    }
}
