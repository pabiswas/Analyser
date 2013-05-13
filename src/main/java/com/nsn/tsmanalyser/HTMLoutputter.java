/**
 * 
 */
package com.nsn.tsmanalyser;

import java.io.File;

/**
 * @author pabiswas
 *
 */
public class HTMLoutputter implements IOutputter {

	/* (non-Javadoc)
	 * @see tsmAnalyser.outputter#generateOutput()
	 */
	@Override
	public void generateOutput() throws Throwable {
		File xmlFile  = new File("FinalXML.xml");
		File xsltFile = utils.readResource("/Stylesheet/HTMLXSLT.xslt", "tmpxsltFile");
		
		utils.applyXslt(xmlFile, xsltFile, "ResultFinal.html");
	}
}
