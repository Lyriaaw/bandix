package com.juniorisep.bandix;

import org.xml.sax.SAXException;

import javax.xml.parsers.ParserConfigurationException;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Main {


    public static void main(String args[]) {
//        try {
//            test();
//        } catch (CloneNotSupportedException e) {
//            e.printStackTrace();
//        }
//        return;


        try {
            Docx docx = new Docx("test.docx", "test_generated.docx");
            docx.open();
            docx.replace(getMap());
            docx.manageTables(getTableMap());
            docx.close();



        } catch (IOException e) {
            e.printStackTrace();
        } catch (SAXException e) {
            e.printStackTrace();
        } catch (ParserConfigurationException e) {
            e.printStackTrace();
        }

    }



    public static Map<String, String> getMap() {
        Map<String, String> map = new HashMap<>();

        map.put("SECTION", "Junior ISEP");
        map.put("YEAR", "A2");
        map.put("SCHOOL", "ISEP");
        map.put("TEST" , "Hello World");

        return map;
    }

    public static List<Map<String, String>> getTableMap() {
        List<Map<String, String>> tableMap = new ArrayList<Map<String, String>>();

        Map<String, String> map;


        map = new HashMap<>();
        map.put("FIRSTNAME", "Amalric");
        map.put("LASTNAME", "Lombard de Buffières");
        tableMap.add(map);

        map = new HashMap<>();
        map.put("FIRSTNAME", "Thomas");
        map.put("LASTNAME", "Rivière");
        tableMap.add(map);

        map = new HashMap<>();
        map.put("FIRSTNAME", "Alexis");
        map.put("LASTNAME", "Falempin");
        tableMap.add(map);

        map = new HashMap<>();
        map.put("FIRSTNAME", "Clara");
        map.put("LASTNAME", "Simmat");
        tableMap.add(map);

        map = new HashMap<>();
        map.put("FIRSTNAME", "Antoine");
        map.put("LASTNAME", "Poussot");
        tableMap.add(map);

        map = new HashMap<>();
        map.put("FIRSTNAME", "Aymeric");
        map.put("LASTNAME", "de Javel");
        tableMap.add(map);

        map = new HashMap<>();
        map.put("FIRSTNAME", "Ilan");
        map.put("LASTNAME", "Abtibol");
        tableMap.add(map);


        return tableMap;

    }



}
