/**
 * 
 */
package com.nsn.tsmanalyser;

import java.io.File;

/**
 * @author pabiswas
 *
 */
public class TEXTOutputter implements IOutputter {

	/* (non-Javadoc)
	 * @see tsmAnalyser.IOutputter#generateOutput()
	 */
	@Override
	public void generateOutput() throws Throwable {
		File xmlFile  = new File("FinalXML.xml");
		File xsltFile = utils.readResource("/Stylesheet/TextXSLT.xslt", "tmpxsltFile");
		
		utils.applyXslt(xmlFile, xsltFile, "ResultFinal.html");                
	}
}
