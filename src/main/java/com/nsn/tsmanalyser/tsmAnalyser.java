package com.nsn.tsmanalyser;

import java.io.*;
import java.util.HashMap;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import javax.xml.xpath.XPathConstants;
import javax.xml.xpath.XPathFactory;
import org.w3c.dom.*;
import org.xml.sax.InputSource;
import org.apache.xerces.parsers.DOMParser;

/**
 * @author pabiswas
 *
 */
public class tsmAnalyser {

    public static void main(String[] args) throws Throwable {
        if (args.length < 1) {
            System.out.println("Usage: ");
            System.out.println("java -jar xmlProcessing <SuiteName>");
            return;
        }

        String suiteName = args[0];
        System.out.println("Suite name : " + suiteName);
        File xmlFile = new File(suiteName);

        File xsltFile = utils.readResource("/Stylesheet/transform.xsl", "tmpxsltFile");

        utils.applyXslt(xmlFile, xsltFile, "Result.xml");
        //---------------------- XSLT Transformation - end --------------------//

        generateStatistics(xmlFile);

        //------- Load the new xml "Result.xml" and process the results -------//
        DOMParser parser = new DOMParser();
        parser.parse("Result.xml");
        Document doc = parser.getDocument();
        
        ApplicationFactory.getInstance().getXMLProcessor().processXML(doc);
        
        //--- Use a Transformer for output ---//
        TransformerFactory tFactory = TransformerFactory.newInstance();
        Transformer transformer = tFactory.newTransformer();

        DOMSource source = new DOMSource(doc);
        StreamResult FinalXmlFile = new StreamResult(new File("FinalXML.xml"));
        transformer.transform(source, FinalXmlFile);

        //--- Get the HashMap containing the different errors and the values ---//
        HashMap<String, Integer> hmap = ApplicationFactory.getInstance().getErrorProcessor().getErrorMap();

        //--- Generate the charts ---//
        utils.generateCharts(hmap);

        //---- Generate output ----//
        ApplicationFactory.getInstance().getOutputter().generateOutput();
    }

    private static void generateStatistics(File xmlFile) throws Throwable {
        XPathFactory factory = XPathFactory.newInstance();
        InputSource is = new InputSource(new FileInputStream(xmlFile));
        DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
//		dbf.setFeature("http://apache.org/xml/features/nonvalidating/load-external-dtd", false);
        DocumentBuilder db = dbf.newDocumentBuilder();
        Element root = db.parse(is).getDocumentElement();

        NodeList numOfTCs = (NodeList) factory.newXPath().evaluate("/TestSuiteCollection/TestSuite[starts-with(@name,'TESTCASE') or starts-with(@name, 'TestCase')]/TestCase", root, XPathConstants.NODESET);

        NodeList numOfFailedTCs = (NodeList) factory.newXPath().evaluate("/TestSuiteCollection/TestSuite[starts-with(@name,'TESTCASE')]/TestCase[not(attribute::active='false')]/TestResult[last()][not(attribute::passed='true')]", root, XPathConstants.NODESET);

        NodeList numOfTCPassed = (NodeList) factory.newXPath().evaluate("/TestSuiteCollection/TestSuite[starts-with(@name,'TESTCASE')]/TestCase[not(attribute::active='false')]/TestResult[last()][attribute::passed='true']", root, XPathConstants.NODESET);

        Integer numOfSkippedTCs = numOfTCs.getLength() - numOfTCPassed.getLength() - numOfFailedTCs.getLength();

        System.out.println("Total number of TCs         : " + numOfTCs.getLength());
        System.out.println("Total number of TCs passed  : " + numOfTCPassed.getLength());
        System.out.println("Total number of TCs failed  : " + numOfFailedTCs.getLength());
        System.out.println("Total number of TCs skipped : " + numOfSkippedTCs);

        HashMap<String, Integer> statisticsMap = new HashMap<String, Integer>();
        statisticsMap.put("Skipped " + numOfSkippedTCs, numOfSkippedTCs);
        statisticsMap.put("Failed " + numOfFailedTCs.getLength(), numOfFailedTCs.getLength());
        statisticsMap.put("Passed " + numOfTCPassed.getLength(), numOfTCPassed.getLength());

        utils.generatePieChart(statisticsMap);
    }
}