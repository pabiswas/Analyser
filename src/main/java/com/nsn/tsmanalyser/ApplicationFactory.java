/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.nsn.tsmanalyser;

import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;

/**
 *
 * @author pabiswas
 */
public class ApplicationFactory {

    private static ApplicationFactory instance = null;
    private static IErrorProcessor m_ErrorProcessor;
    private static IOutputter m_Outputter;
    private static IXMLProcessor m_XMLProcessor;

    private ApplicationFactory() {
    }

    public static ApplicationFactory getInstance() {
        if (instance == null) {
            instance = new ApplicationFactory();

            ApplicationContext context = new ClassPathXmlApplicationContext("ApplicationContext.xml");
            m_ErrorProcessor = (IErrorProcessor) context.getBean("Protocol");
            m_Outputter = (IOutputter) context.getBean("Outputter");
            m_XMLProcessor = (IXMLProcessor) context.getBean("XMLProcessor");
        }
        return instance;
    }

    public static IErrorProcessor getErrorProcessor() {
        return m_ErrorProcessor;
    }

    public static IOutputter getOutputter() {
        return m_Outputter;
    }

    public static IXMLProcessor getXMLProcessor() {
        return m_XMLProcessor;
    }
}
