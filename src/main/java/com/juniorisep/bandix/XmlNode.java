package com.juniorisep.bandix;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class XmlNode {

    private String name;

    private List<XmlNode> children;

    private String text;

    private Map<String, String> attributes;

    public XmlNode() {
    }

    public XmlNode(String name) {
        this.name = name;
    }

    public XmlNode(String name, List<XmlNode> children) {
        this.name = name;
        this.children = children;
    }

    public XmlNode(String name, String text) {
        this.name = name;
        this.text = text;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public List<XmlNode> getChildren() {
        return children;
    }

    public void setChildren(List<XmlNode> children) {
        this.children = children;
    }

    public void addChild(XmlNode node) {
        if (this.children == null) this.children = new ArrayList<>();

        this.children.add(node);
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }

    public Map<String, String> getAttributes() {
        return attributes;
    }

    public void setAttributes(Map<String, String> attributes) {
        this.attributes = attributes;
    }

    public void addAttribute(String key, String value) {
        if (this.attributes == null) {
            this.attributes = new HashMap<>();
        }

        this.attributes.put(key, value);
    }







    public void replace(Map<String, String> mapping) {

        if (DocxNodeName.PARAGRAPH.equals(name)) {
            getReplacements(mapping);

//            replacements.forEach(replacement -> {
//                System.out.println(replacement.toString());
//                replacement.apply();
//            });

        } else if ("w;r".equals(name)) {

        } else if ("w:t".equals(name)) {

        } else if (children != null) {
            children.forEach(child -> {
                child.replace(mapping);
            });
        }

        /*
        else if (text != null && !text.isEmpty()) {
            mapping.forEach((key, value) -> {
                if ((":" + key + ":").equals(text)) {
                    text = value;
                }
            });
        }
         */
    }

    public String getParagraph() {
        StringBuilder textBuilder = new StringBuilder();

        if (children != null) {
            for (XmlNode currentNode : children) {
                if (DocxNodeName.RUN.equals(currentNode.getName())) {
                    textBuilder.append(currentNode.getParagraph());
                } else if (DocxNodeName.TEXT.equals(currentNode.getName())) {
                    textBuilder.append(currentNode.getText());
                }
            }
        }

        return textBuilder.toString();
    }

    public String getGlobalText() {
        StringBuilder builder = new StringBuilder();

        if (children != null) {
            for (XmlNode currentNode : children) {
                if (currentNode.getText() != null && !currentNode.getText().isEmpty()) {
                    builder.append(currentNode.getText());
                } else {
                    builder.append(currentNode.getGlobalText());
                }
            }
        }

        return builder.toString();
    }

    public void getReplacements(Map<String, String> mapping) {


        for (Map.Entry<String, String> entry : mapping.entrySet()) {    // running throw the whole keymap
            boolean found = true;

            while (found) {
                found = false;
                String rawText = getParagraph();
                String currentKey = ":" + entry.getKey() + ":";
                int currentKeySize = currentKey.length();

                for (int it = 0; it < rawText.length() - currentKeySize + 1; it++) {    // Running throw the raw text and finding every place we need to change a key to the value
                    String substring = rawText.substring(it, it + currentKeySize);
                    System.out.println(substring + " - " + it + " - " + currentKey);
                    if (currentKey.equals(substring)) {
                        ParagraphReplacement paragraphReplacement = new ParagraphReplacement(it, it + currentKeySize, entry.getKey(), entry.getValue(), this);
//                        System.out.println(paragraphReplacement);
                        paragraphReplacement.apply();
                        found = true;
                        System.out.println("Found");
                        break;
                    }
                }

                System.out.println(getParagraph());
                System.out.println();
                System.out.println();
            }


        }

    }



    protected XmlNode clone() throws CloneNotSupportedException {
        XmlNode node = new XmlNode();
        node.setText(text);
        node.setAttributes(attributes);
        node.setName(name);

        if (children != null && children.size() > 0) {
            for (XmlNode currentNode : children) {
                node.addChild(currentNode.clone());
            }
        }

        return node;
    }
}
