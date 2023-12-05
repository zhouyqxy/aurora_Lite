package com.aurora.util;

import org.w3c.dom.Document;
import org.w3c.dom.NodeList;
import org.xml.sax.InputSource;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.xpath.XPath;
import javax.xml.xpath.XPathConstants;
import javax.xml.xpath.XPathFactory;
import java.io.StringReader;
public class XmlParser {
    public static void main(String[] args) throws Exception {
        String xmlString = "<Dcoument><EAA></EAA><EBB><EB02><EB02A><EB02AS01>0</EB02AS01><EB02AJ01>0</EB02AJ01><EB02R01/><EB02AS02>0</EB02AS02><EB02AJ02>0</EB02AJ02><EB02R02/><EB02AJ03>0</EB02AJ03><EB02AJ04>0</EB02AJ04><EB02AJ05>0</EB02AJ05><EB02AS03>4</EB02AS03><EB02AH><EB02AD01>0</EB02AD01><EB02AD02>1</EB02AD02><EB02AS04>1</EB02AS04><EB02AJ06>1300011</EB02AJ06></EB02AH><EB02AH><EB02AD01>0</EB02AD01><EB02AD02>3</EB02AD02><EB02AS04>1</EB02AS04><EB02AJ06>13000031</EB02AJ06></EB02AH><EB02AH><EB02AD01>2</EB02AD01><EB02AD02>1</EB02AD02><EB02AS04>1</EB02AS04><EB02AJ06>13000211</EB02AJ06></EB02AH><EB02AH><EB02AD01>2</EB02AD01><EB02AD02>0</EB02AD02><EB02AS04>1</EB02AS04><EB02AJ06>1300201</EB02AJ06></EB02AH></EB02A></EB02></EBB></Dcoument>";
        DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
        DocumentBuilder db = dbf.newDocumentBuilder();
        Document doc = db.parse(new InputSource(new StringReader(xmlString)));
//        DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
//        DocumentBuilder builder = factory.newDocumentBuilder();
//        Document doc = builder.parse("path/to/xml/file.xml");
        XPath xPath = XPathFactory.newInstance().newXPath();
        String expression = "//EB02AH[EB02AD01=2 and EB02AD02=1]/EB02AJ06";
        NodeList nodeList = (NodeList) xPath.compile(expression).evaluate(doc, XPathConstants.NODESET);
        for (int i = 0; i < nodeList.getLength(); i++) {
            System.out.println(nodeList.item(i).getTextContent());
        }
    }
}