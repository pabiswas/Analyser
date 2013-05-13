/**
 * 
 */
package com.nsn.tsmanalyser;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.util.HashMap;
import java.util.Map;
import java.util.StringTokenizer;

/**
 * @author pabiswas
 * @deprecated Spring Framework is now used to initialize the Outputter Object 
 * and Protocol Object. See ApplicationContext.xml
 */
public class ErrorFactory {
	private static ErrorFactory factory = null;
	private Map<String, IErrorProcessor> errorObjMap;
	private Map<String, IOutputter>		 outputterObjMap;
	private String errorObjId;
	private String outputterObjId;
	
	private ErrorFactory() throws Throwable
	{
		File aFile = new File("configuration.txt");
		BufferedReader input =  new BufferedReader(new FileReader(aFile));
		String line;
		
		try 
		{
		  while (( line = input.readLine()) != null)
		  {
			  if(line.contains("PROTOCOL"))
			  {
				  StringTokenizer tokens = new StringTokenizer(line, ":");
				  String protocol = tokens.nextToken();
				  protocol = tokens.nextToken();
				  errorObjId = protocol;
			  }
			  else if(line.contains("OUTPUT"))
			  {
				  StringTokenizer tokens = new StringTokenizer(line, ":");
				  String output = tokens.nextToken();
				  output = tokens.nextToken();
				  outputterObjId = output;
			  }
		  }
		}
		finally 
		{
		  input.close();
		}
		
		errorObjMap 	= new HashMap<String, IErrorProcessor>();
		outputterObjMap = new HashMap<String, IOutputter>();
	}
	
	public static ErrorFactory getInstance() throws Throwable
	{
		if(factory == null)
		{
			factory = new ErrorFactory();
		}
		return factory;
	}
	
	public void Register(String id, IErrorProcessor errorProcesserObj)
	{
		if(errorObjMap.containsKey(id) == false)
		{
			errorObjMap.put(id, errorProcesserObj);
		}
	}
	
	public void Register(String id, IOutputter outputterObj)
	{
		if(outputterObjMap.containsKey(id) == false)
		{
			outputterObjMap.put(id, outputterObj);
		}
	}
	
	public  IErrorProcessor CreateProtocolObject()
	{
		if(errorObjMap.containsKey(errorObjId))
		{
			return errorObjMap.get(errorObjId);
		}
		return null;
	}
	
	public IOutputter CreateOutputterObject()
	{
		if(outputterObjMap.containsKey(outputterObjId))
		{
			return outputterObjMap.get(outputterObjId);
		}
		return null;		
	}
}
