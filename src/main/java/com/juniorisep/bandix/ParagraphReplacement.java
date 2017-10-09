package com.juniorisep.bandix;

public class ParagraphReplacement {
    private int startIndex;
    private int endIndex;
    private String key;
    private String value;
    private XmlNode paragraph;

    public ParagraphReplacement(int startIndex, int endIndex, String key, String value, XmlNode paragraph) {
        this.startIndex = startIndex;
        this.endIndex = endIndex;
        this.key = ":" + key + ":";
        this.value = value;
        this.paragraph = paragraph;
    }

    public int getStartIndex() {
        return startIndex;
    }

    public void setStartIndex(int startIndex) {
        this.startIndex = startIndex;
    }

    public int getEndIndex() {
        return endIndex;
    }

    public void setEndIndex(int endIndex) {
        this.endIndex = endIndex;
    }

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }

    public XmlNode getParagraph() {
        return paragraph;
    }

    public void setParagraph(XmlNode paragraph) {
        this.paragraph = paragraph;
    }

    @Override
    public String toString() {
        return "ParagraphReplacement{" +
                "startIndex=" + startIndex +
                ", endIndex=" + endIndex +
                ", key='" + key + '\'' +
                ", value='" + value + '\'' +
                ", paragraph=" + paragraph.getParagraph() +
                '}';
    }





    public void apply() {
        int currentIndex = 0;
        int charPlaced = 0;
        boolean replacementFinished = false;
        for (XmlNode currentRun : paragraph.getChildren()) {
            if (!DocxNodeName.RUN.equals(currentRun.getName())) continue;

            for (XmlNode currentText : currentRun.getChildren()) {
                if (replacementFinished) continue;
                if (!DocxNodeName.TEXT.equals(currentText.getName())) continue;
                if (currentText.getText() == null || currentText.getText().isEmpty()) continue;


                String text = currentText.getText();
                int textLength = text.length();

                int charToPlace = 0;
                StringBuilder partReplaced = new StringBuilder();

                int it;
                for (it = 0; it < textLength; it++) {

//                    if (currentIndex < startIndex){
//                        System.out.println(currentIndex);
//                        currentIndex++;
//                        continue;
//                    }

//                    System.out.println("");
//                    System.out.println(text);
                    System.out.println("it : " + it + " - " + text.substring(it) + " - " + currentIndex);
//                    System.out.println("ch : " + charToPlace + " - " + key.substring(charToPlace) + " - " + key.length() + " - " + value.length());


                    if (text.charAt(it) == key.charAt(charToPlace) && currentIndex >= startIndex) {
                        partReplaced.append(text.charAt(it));
                        charToPlace++;
                    } else {
                        charToPlace = 0;
                        partReplaced = new StringBuilder();
                    }



                    if (charToPlace >= key.length() || (it == textLength - 1 && charToPlace > 0)) {

//                        System.out.println(text + " - " + partReplaced.toString() + " - " + value.substring(0, Math.min(charToPlace, value.length())));

                        String valueToWrite = "";
                        if (charToPlace > value.length() && value.length() == 1 && charToPlace < key.length()) {
                            valueToWrite = "";
                            key = key.substring(charToPlace);
//                            System.out.println("1");
                        } else if (charToPlace == key.length() || charToPlace + charPlaced == key.length()) {
                            valueToWrite = value;
//                            System.out.println("2");
                            replacementFinished = true;
                        } else {
//                            System.out.println("3");
                            valueToWrite = value.substring(0, 1);
                            value = value.substring(1);
                            key = key.substring(charToPlace);
                        }

                        System.out.println("Replacing " + partReplaced.toString() + " by " + valueToWrite);

                        text = text.replaceFirst(partReplaced.toString(), valueToWrite);
                        currentText.setText(text);
                        charPlaced += charToPlace;

                        break;
                    }



                    currentIndex++;
                }


            }

        }

    }




}
