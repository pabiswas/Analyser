package com.nsn.tsmanalyser;

import java.io.*;
import java.util.HashMap;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import javax.xml.xpath.XPathConstants;
import javax.xml.xpath.XPathFactory;

import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;
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

        ApplicationContext context = new ClassPathXmlApplicationContext("ApplicationContext.xml");
        IErrorProcessor errorProcessor = (IErrorProcessor) context.getBean("Protocol");
        IOutputter outputter = (IOutputter) context.getBean("Outputter");
        IXMLProcessor xmlProcessor = (IXMLProcessor) context.getBean("XMLProcessor");

        File xsltFile = utils.readResource("/Stylesheet/transform.xsl", "tmpxsltFile");

        utils.applyXslt(xmlFile, xsltFile, "Result.xml");
        //---------------------- XSLT Transformation - end --------------------//

        generateStatistics(xmlFile);

        //------- Load the new xml "Result.xml" and process the results -------//
        DOMParser parser = new DOMParser();
        parser.parse("Result.xml");
        Document doc = parser.getDocument();
        
//        xmlProcessor.processXML(doc);
        
        Element root = doc.getDocumentElement();
        NodeList FailedTestCases = root.getElementsByTagName("TestCase");

        for (int i = 0; i < FailedTestCases.getLength(); i++) {
            //--- Print Test Case Name ---//
            System.out.println("\n\nTest Case Name : " + ((Element) FailedTestCases.item(i)).getElementsByTagName("TCName").item(0).getTextContent());

            //--- Print Test Case Title ---//
            String TestCaseDescription = ((Element) FailedTestCases.item(i)).getElementsByTagName("TestCaseDescription").item(0).getTextContent();

            String title = utils.getTitle(TestCaseDescription);
            System.out.println(title);

            //--- Get the error text and get the trace files ---//
            String ErrorText = ((Element) FailedTestCases.item(i)).getElementsByTagName("ResultText").item(0).getTextContent();

            //Pattern p = Pattern.compile("C.*reports.*trc[^.]");
            Pattern p = Pattern.compile("C.*reports.*trc");
            //Pattern p = Pattern.compile("(C)(.*)(reports)(.*)(msg)");
            Matcher m = p.matcher(ErrorText);

            StringBuilder traceFile = new StringBuilder();
            StringBuilder messageFlow = new StringBuilder();

            while (m.find()) {
                String str = (String) m.group();
                String fileName = str.substring(0, str.length());

                System.out.println("Reading file ->" + fileName + "<-"); // of length " + str.length());

                File aFile = new File(fileName);

                BufferedReader trcFile = new BufferedReader(new FileReader(aFile));
                try {
                    String line = null;

                    while ((line = trcFile.readLine()) != null) {
                        String msg = "";
                        msg = errorProcessor.processLine(line);
                        if (msg != null) {
                            //--- This is some valid message ---//
                            System.out.println(msg);
                            messageFlow.append(msg);
                            messageFlow.append("\n");;

                            if (msg.contains("|                  |")) {
                                msg = msg.replace("|                  |", "|****************|");
                            }

                            //------- Create MessageFlow node which contains the Message ----------//
                            Element MessageFlow = doc.createElement("MessageFlow");
                            Text MessageText = doc.createTextNode(msg);
                            MessageFlow.appendChild(MessageText);
                            FailedTestCases.item(i).appendChild(MessageFlow);
                            //---------------------------------------------------------------------//
                        }
                    }
                } finally {
                    /**
                     * Not the proper place to close file, what if file open
                     * fails?
                     */
                    trcFile.close();
                }
                traceFile.append(fileName);
                traceFile.append("\n");

                //------- Create TraceFile node which contains the Trace File location ----------//
                Element TraceFile = doc.createElement("TraceFile");
                Text TraceText = doc.createTextNode(fileName);
                TraceFile.appendChild(TraceText);
                FailedTestCases.item(i).appendChild(TraceFile);
                //-----------------------------------------------------------------------------------//
            }

            //------- Create the Title node which contains the Title ----------//
            Element Title = doc.createElement("Title");
            Text TitleText = doc.createTextNode(title);
            Title.appendChild(TitleText);
            FailedTestCases.item(i).appendChild(Title);
            //-----------------------------------------------------------------------------------//
        }

        //--- Use a Transformer for output ---//
        TransformerFactory tFactory = TransformerFactory.newInstance();
        Transformer transformer = tFactory.newTransformer();

        DOMSource source = new DOMSource(doc);
        StreamResult FinalXmlFile = new StreamResult(new File("FinalXML.xml"));
        transformer.transform(source, FinalXmlFile);

        //--- Get the HashMap containing the different errors and the values ---//
        HashMap<String, Integer> hmap = errorProcessor.getErrorMap();

        //--- Generate the charts ---//
        utils.generateCharts(hmap);

        //---- Generate output ----//
        outputter.generateOutput();
    }

    private static void generateStatistics(File xmlFile) throws Throwable {
        XPathFactory factory = XPathFactory.newInstance();
        InputSource is = new InputSource(new FileInputStream(xmlFile));
        DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
//		dbf.setFeature("http://apache.org/xml/features/nonvalidating/load-external-dtd", false);
        DocumentBuilder db = dbf.newDocumentBuilder();
        Element root = db.parse(is).getDocumentElement();

        NodeList numOfTCs = (NodeList) factory.newXPath().evaluate("/TestSuiteCollection/TestSuite[starts-with(@name,'TESTCASE')]/TestCase", root, XPathConstants.NODESET);

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