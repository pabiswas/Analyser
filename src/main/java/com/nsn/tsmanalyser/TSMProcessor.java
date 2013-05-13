/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.nsn.tsmanalyser;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;
import org.w3c.dom.Text;

/**
 *
 * @author pabiswas
 */
public class TSMProcessor implements IXMLProcessor {

    public void processXML(Document doc) {
        Element root = doc.getDocumentElement();
        NodeList FailedTestCases = root.getElementsByTagName("TestCase");

        ApplicationContext context = new ClassPathXmlApplicationContext("ApplicationContext.xml");
        IErrorProcessor errorProcessor = (IErrorProcessor) context.getBean("Protocol");

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

                BufferedReader trcFile = null;
                try {
                    trcFile = new BufferedReader(new FileReader(aFile));
                } catch (FileNotFoundException ex) {
                    Logger.getLogger(TSMProcessor.class.getName()).log(Level.SEVERE, null, ex);
                }

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
                } catch (IOException ex) {
                    Logger.getLogger(TSMProcessor.class.getName()).log(Level.SEVERE, null, ex);
                } catch (Throwable ex) {
                    Logger.getLogger(TSMProcessor.class.getName()).log(Level.SEVERE, null, ex);
                } finally {
                    try {
                        trcFile.close();
                    } catch (IOException ex) {
                        Logger.getLogger(TSMProcessor.class.getName()).log(Level.SEVERE, null, ex);
                    }
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
    }
}
