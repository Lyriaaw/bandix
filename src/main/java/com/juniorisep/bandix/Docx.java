package com.juniorisep.bandix;

import org.apache.commons.io.FileUtils;
import org.w3c.dom.*;
import org.xml.sax.SAXException;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import java.io.*;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;
import java.util.zip.ZipOutputStream;

public class Docx {

    private String inputFilename;
    private String outputFilename;

    private List<String> files = new ArrayList<>();

    public static String TEMP_FOLDER = "tmp/bandix/document_generation/";
    private String fileFolder;

    private XmlNode root;



    public Docx(String inputFilename, String outputFilename) throws FileNotFoundException {
        this.inputFilename = inputFilename;
        this.outputFilename = outputFilename;

        fileFolder = TEMP_FOLDER + outputFilename + ".d/";
    }



    public String getInputFilename() {
        return inputFilename;
    }

    public void setInputFilename(String inputFilename) {
        this.inputFilename = inputFilename;
    }

    public String getOutputFilename() {
        return outputFilename;
    }

    public void setOutputFilename(String outputFilename) {
        this.outputFilename = outputFilename;
    }

    public List<String> getFiles() {
        return files;
    }

    public void setFiles(List<String> files) {
        this.files = files;
    }

    public XmlNode getRoot() {
        return root;
    }

    public void setRoot(XmlNode root) {
        this.root = root;
    }


    /**
     * Open the docx and place xml files in the appropriated folder
     * @throws IOException
     */
    public void open() throws IOException {
        ZipInputStream zis = new ZipInputStream(new FileInputStream(inputFilename));

        byte[] buffer = new byte[1024];

        File folder = new File(fileFolder);
        if (!folder.exists()) {
            folder.mkdirs();
        }


        ZipEntry entry = zis.getNextEntry();

        while (entry != null) {

            String filename = entry.getName();
            files.add(filename);


            File currentFile = new File(fileFolder + filename);
            new File(currentFile.getParent()).mkdirs();
            FileOutputStream fos = new FileOutputStream(currentFile);

            int len;
            while ((len = zis.read(buffer)) > 0) {
                fos.write(buffer, 0, len);
            }

            fos.close();

            entry = zis.getNextEntry();
        }

        zis.closeEntry();
        zis.close();
    }


    /**
     * Create the new docx (as a zip) and place all the xml files into it
     * @throws IOException
     */
    public void close() throws IOException {
        FileOutputStream fos = new FileOutputStream(outputFilename);
        ZipOutputStream zos = new ZipOutputStream(fos);

        for (String filename : files) {
            File file = new File(fileFolder + filename);
            FileInputStream fis = new FileInputStream(file);
            ZipEntry zipEntry = new ZipEntry(filename);

            zos.putNextEntry(zipEntry);

            byte[] bytes = new byte[1024];
            int length;
            while ((length = fis.read(bytes)) >= 0) {
                zos.write(bytes, 0, length);
            }

            zos.closeEntry();
            fis.close();

        }

        zos.close();

        deleteTempFiles();
    }

    public void deleteTempFiles() throws IOException {
//        FileUtils.deleteDirectory(new File(fileFolder));
    }




    public void replace(Map<String, String> mapping) throws ParserConfigurationException, IOException, SAXException {

        for (String file : files) {
            if (file.contains("[Content_Types]") || !file.contains(".xml")) continue;
            System.out.println(file);

            DocumentParser parser = new DocumentParser(fileFolder + file);
            parser.replace(mapping);

        }
    }


    public void manageTables(List<Map<String, String>> tableMap) throws ParserConfigurationException, SAXException, IOException {
        for (String file : files) {
            if (file.contains("[Content_Types]") || !file.contains(".xml")) continue;
            System.out.println(file);

            DocumentParser parser = new DocumentParser(fileFolder + file);
            parser.manageTables(tableMap);

        }
    }












    public String analyseNode(Node n, String tab) {
        if (!(n instanceof Element)) return n.getTextContent();


        NodeList list = n.getChildNodes();

        System.out.print(tab + "<" + n.getNodeName() + ">");

        if ("w:t".equals(n.getNodeName())) {
            System.out.print(n.getTextContent());
        } else {
            System.out.println();
        }

        for (int it = 0; it < list.getLength(); it++) {
            Node currentNode = list.item(it);

            if (currentNode instanceof Element) {
                analyseNode(currentNode, tab + " ");
            }

        }

        System.out.println(tab + "</" + n.getNodeName() + ">");

        return "";
    }








}
